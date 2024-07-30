package ru.valkovets.mephisoty.db.service.season.scoring;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.StageScoreForParticipantDto;
import ru.valkovets.mephisoty.api.dto.season.StageScoresAllDto;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;
import ru.valkovets.mephisoty.db.projection.special.StageScoreShortProj;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.StageScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

  final Page<UserSimpleGroupProj> participantsPage = userRepository.findBy(
      Specification.where(participantsFilter)
                   .and((root, query, builder) ->
                            builder.or(
                                builder.equal(root.get("credentials").get("role"), UserRole.PARTICIPANT),
                                builder.equal(root.get("credentials").get("role"), UserRole.ADMIN))),
      q -> q.sortBy(participantSort == null ? Sort.unsorted() : participantSort)
            .as(UserSimpleGroupProj.class)
            .page(PageRequest.of(page, size)));

  final LinkedHashMap<Long, UserSimpleGroupProj> participantsById =
      participantsPage.get().collect(Collectors.toMap(UserSimpleGroupProj::getId, e -> e, (u1, u2) -> u1, LinkedHashMap::new));
  final long participantsTotal = participantsPage.getTotalElements();
  final int participantsCount = participantsById.size();

  final LinkedHashMap<Long, StageScoreForParticipantDto> stageScoreByParticipantId = new LinkedHashMap<>(participantsCount);
  for (final UserSimpleGroupProj participant : participantsById.sequencedValues()) { // Может на стримах быстрее будет, но мне так не кажется
    final Long participantId = participant.getId();
    final HashMap<Long, Float> scoreByExpertId =
        new HashMap<>(8);  // todo Уточнить сколько экспертов обычно оценивает одного человека
    stageScoreByParticipantId.put(participantId, new StageScoreForParticipantDto(participant, scoreByExpertId));
  }

  final Set<StageScoreShortProj> scores = scoreRepository.getAllByParticipant_IdInAndStage_IdIn(
      participantsById.keySet(), stageIds, StageScoreShortProj.class);
  for (final StageScoreShortProj score : scores) {
    final Long participantId = score.getParticipant().getId();
    final Long stageId = score.getStage().getId();
    final Float scoreValue = score.getScore();

    stageScoreByParticipantId.get(participantId).scoreByStageId().put(stageId, scoreValue);
  }

  return new StageScoresAllDto(stageScoreByParticipantId.sequencedValues(), stages, participantsTotal);
}


@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public StageScore setScore(final Long stageId, final Long participantId, final Float score) {
  final StageScore stageScore =
      scoreRepository.findByStage_IdAndParticipant_Id(stageId, participantId)
                     .orElse(StageScore.builder()
                                       .stage(Stage.builder().id(stageId).build())
                                       .participant(User.builder().id(participantId).build())
                                       .build());
  stageScore.setScoreByStageFormula(score);
  return scoreRepository.save(stageScore);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void delete(final Long stageId, final Long participantId) {
  scoreRepository.deleteByStage_IdAndParticipant_Id(stageId, participantId);
}
}
