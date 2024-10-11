package ru.valkovets.mephisoty.db.service.season.scoring;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.dto.season.StageScoresAllDto;
import ru.valkovets.mephisoty.application.lifecycle.ticker.StageScoreTicker;
import ru.valkovets.mephisoty.db.manager.ParticipantsManager;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore_;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.stagescore.StageScoreShortProj;
import ru.valkovets.mephisoty.db.projection.special.stagescore.StageScoreTableProj;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.StageScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StageScoreService {
private final StageScoreRepository scoreRepository;
private final StageRepository stageRepository;
private final UserRepository userRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public StageScoresAllDto getAllForSeason(final int page, final int size, final Long seasonId,
                                         final Specification<User> participantsFilter,
                                         final Sort participantSort) {
  final List<IdTitleProj> stages = stageRepository.getAllBySeason_IdOrderByTitleAsc(seasonId, IdTitleProj.class);
  final List<Long> stageIds = stages.stream().map(IdTitleProj::getId).toList();

  final ParticipantsManager.ParticipantsTableResult result = ParticipantsManager
      .getParticipantsTableResult(userRepository, page, size, participantsFilter, participantSort);

  final Set<StageScoreShortProj> scores = scoreRepository.getAllByParticipant_IdInAndStage_IdIn(
      result.participantsById().keySet(), stageIds, StageScoreShortProj.class);
  for (final StageScoreShortProj score : scores) {
    final Long participantId = score.getParticipant().getId();
    final Long stageId = score.getStage().getId();
    final ScoreIdCommentDto scoreValue = new ScoreIdCommentDto(score);

    result.scoresByParticipantId().get(participantId).scoreById().put(stageId, scoreValue);
  }

  return new StageScoresAllDto(result.scoresByParticipantId().sequencedValues(),
                               stages,
                               result.participantsTotal());
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public Page<StageScoreTableProj> getAll(final int page, final int size, final Long stageId,
                                        final Specification<StageScore> filter,
                                        final Sort sort) {
  final Page<StageScoreTableProj> buf = scoreRepository.findBy(
      Specification.where(filter)
                   .and((root, query, builder) -> builder.equal(root.get(StageScore_.stage).get(Stage_.id), stageId)),
      q -> q.sortBy(sort == null ? Sort.by(StageScore_.SCORE_BY_STAGE_FORMULA) : sort)
            .as(StageScoreTableProj.class)
            .page(PageRequest.of(page, size)));
  buf.forEach(score -> {
    final IdTitleProj group = score.getParticipant().getGroup();
    if (group != null) {
      final String title = group.getTitle();
    }
  });
  return buf;
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void evaluateFor(final Long stageId) {
  StageScoreTicker.addForEvaluation(stageId);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void setScore(final Long stageId, final Long participantId, final ScoreIdCommentDto score) {
  final StageScore stageScore =
      scoreRepository.findByStage_IdAndParticipant_Id(stageId, participantId)
                     .orElse(StageScore.builder()
                                       .stage(Stage.builder().id(stageId).build())
                                       .participant(User.builder().id(participantId).build())
                                       .build());
  stageScore.setComment(score.comment());
  stageScore.setInitialScore(score.initialScore());
  stageScore.setScoreByStageFormula(score.score());
  scoreRepository.save(stageScore);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void delete(final Long stageId, final Long participantId) {
  scoreRepository.deleteByStage_IdAndParticipant_Id(stageId, participantId);
}
}
