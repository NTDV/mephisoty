package ru.valkovets.mephisoty.db.service.season.scoring;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.dto.season.StageScoresAllDto;
import ru.valkovets.mephisoty.db.manager.ParticipantsManager;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.StageScoreShortProj;
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
public StageScoresAllDto getAll(final int page, final int size, final Long seasonId,
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
public StageScore setScore(final Long stageId, final Long participantId, final ScoreIdCommentDto score) {
  final StageScore stageScore =
      scoreRepository.findByStage_IdAndParticipant_Id(stageId, participantId)
                     .orElse(StageScore.builder()
                                       .stage(Stage.builder().id(stageId).build())
                                       .participant(User.builder().id(participantId).build())
                                       .build());
  stageScore.setComment(score.comment());
  stageScore.setScoreByStageFormula(score.score());
  return scoreRepository.save(stageScore);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void delete(final Long stageId, final Long participantId) {
  scoreRepository.deleteByStage_IdAndParticipant_Id(stageId, participantId);
}
}
