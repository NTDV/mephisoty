package ru.valkovets.mephisoty.db.service.season.scoring;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.CriteriaScoresAllDto;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.dto.season.StageCriteriasScoresAllDto;
import ru.valkovets.mephisoty.db.manager.ParticipantsManager;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.simple.UserNameIdProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaShortestProj;
import ru.valkovets.mephisoty.db.projection.special.StageCriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CriteriaScoreService {
private final CriteriaScoreRepository scoreRepository;
private final UserRepository userRepository;
private final CriteriaRepository criteriaRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public CriteriaScoresAllDto getAll(final int page, final int size, final Long criteriaId,
                                   final Specification<User> participantsFilter,
                                   final Sort participantSort) {
  final List<UserNameIdProj> experts = getAllExperts();

  final Stage stage = criteriaRepository.findById(criteriaId).orElseThrow().getStage();

  final ParticipantsManager.ParticipantsTableResult result = ParticipantsManager.getParticipantsTableResult(
      userRepository, page, size,
      Specification.where(participantsFilter)
                   .and(((root, query, builder) -> builder.and(
                       builder.equal(root.get(User_.credentials).get("role"), UserRole.PARTICIPANT),
                       builder.equal(root.join(User_.chosenStages).get(Stage_.id), stage.getId())))),
      participantSort);

  final Set<CriteriaScoreShortProj> scores = scoreRepository.getAllByParticipant_IdInAndCriteria_Id(
      result.participantsById().keySet(), criteriaId, CriteriaScoreShortProj.class);
  for (final CriteriaScoreShortProj score : scores) {
    final Long participantId = score.getParticipant().getId();
    final Long expertId = score.getExpert().getId();
    final ScoreIdCommentDto scoreValue = new ScoreIdCommentDto(score);

    result.scoresByParticipantId().get(participantId).scoreById().put(expertId, scoreValue);
  }

  return new CriteriaScoresAllDto(result.scoresByParticipantId().sequencedValues(),
                                  experts,
                                  result.participantsTotal());
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN," +
              "T(ru.valkovets.mephisoty.settings.UserRole).EXPERT)")
public StageCriteriasScoresAllDto getAllForStage(final int page, final int size, final Long stageId,
                                                 final Specification<User> participantsFilter,
                                                 final Sort participantSort) {
  final Long expertId = Credentials.getCurrent().getId();
  final LinkedHashMap<Long, CriteriaShortestProj> criterias = criteriaRepository
      .getAllByStage_IdOrderByTitleAsc(stageId, CriteriaShortestProj.class)
      .stream()
      .collect(Collectors.toMap(CriteriaShortestProj::getId,
                                e -> e,
                                (u1, u2) -> u1,
                                LinkedHashMap::new));

  final ParticipantsManager.ParticipantsTableResult result = ParticipantsManager.getParticipantsTableResult(
      userRepository, page, size,
      Specification.where(participantsFilter)
                   .and(((root, query, builder) -> builder.and(
                       builder.equal(root.get(User_.credentials).get("role"), UserRole.PARTICIPANT),
                       builder.equal(root.join(User_.chosenStages).get(Stage_.id), stageId)))),
      participantSort);

  final Set<StageCriteriaScoreShortProj> scores = scoreRepository.getAllByExpertIdAndParticipant_IdInAndCriteria_IdIn(
      expertId, result.participantsById().keySet(), criterias.keySet(), StageCriteriaScoreShortProj.class);
  for (final StageCriteriaScoreShortProj score : scores) {
    final Long participantId = score.getParticipant().getId();
    final Long criteriaId = score.getCriteria().getId();
    final ScoreIdCommentDto scoreValue = new ScoreIdCommentDto(score);

    result.scoresByParticipantId().get(participantId).scoreById().put(criteriaId, scoreValue);
  }

  return new StageCriteriasScoresAllDto(result.scoresByParticipantId().sequencedValues(),
                                        criterias.sequencedValues(),
                                        result.participantsTotal());
}


private List<UserNameIdProj> getAllExperts() {
  return userRepository.findByCredentials_RoleInOrderBySecondNameAscFirstNameAscThirdNameAsc(
      Set.of(UserRole.ADMIN, UserRole.EXPERT), Pageable.unpaged(), UserNameIdProj.class).toList();
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public CriteriaScore setScore(final Long criteriaId, final Long expertId, final Long participantId,
                              final ScoreIdCommentDto score) {
  return _setScore(criteriaId, expertId, participantId, score);
}

public CriteriaScore _setScore(final Long criteriaId, final Long expertId, final Long participantId,
                              final ScoreIdCommentDto score) {
  final CriteriaScore criteriaScore = scoreRepository
      .findByCriteria_IdAndExpert_IdAndParticipant_Id(criteriaId, expertId, participantId)
      .orElse(CriteriaScore.builder()
                           .criteria(Criteria.builder().id(criteriaId).build())
                           .expert(User.builder().id(expertId).build())
                           .participant(User.builder().id(participantId).build())
                           .build());

  criteriaScore.setComment(score.comment());
  criteriaScore.setScore(score.score());

  return scoreRepository.save(criteriaScore);
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).EXPERT)")
public CriteriaScore setScore(final Long criteriaId, final Long participantId, final ScoreIdCommentDto score) {
  return _setScore(criteriaId, Credentials.getCurrent().getId(), participantId, score);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void delete(final Long criteriaId, final Long expertId, final Long participantId) {
  _delete(criteriaId, expertId, participantId);
}

private void _delete(final Long criteriaId, final Long expertId, final Long participantId) {
  scoreRepository.deleteByCriteria_IdAndExpert_IdAndParticipant_Id(criteriaId, expertId, participantId);
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).EXPERT)")
@Transactional
public void delete(final Long criteriaId, final Long participantId) {
  _delete(criteriaId, Credentials.getCurrent().getId(), participantId);
}
}
