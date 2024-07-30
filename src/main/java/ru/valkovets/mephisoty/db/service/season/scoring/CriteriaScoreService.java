package ru.valkovets.mephisoty.db.service.season.scoring;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.CriteriaScoreForParticipantDto;
import ru.valkovets.mephisoty.api.dto.season.CriteriaScoresAllDto;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CriteriaScoreService {
private final CriteriaScoreRepository scoreRepository;
private final UserRepository userRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public CriteriaScoresAllDto getAll(final int page, final int size, final Long criteriaId,
                                   final Specification<User> participantsFilter,
                                   final Sort participantSort) {
  final List<UserSimpleProj> experts = getAllExperts();

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

  final LinkedHashMap<Long, CriteriaScoreForParticipantDto> criteriaScoresByParticipantId =
      new LinkedHashMap<>(participantsCount);
  for (final UserSimpleGroupProj participant : participantsById.sequencedValues()) { // Может на стримах быстрее будет, но мне так не кажется
    final Long participantId = participant.getId();
    final HashMap<Long, ScoreIdCommentDto> scoreByExpertId =
        new HashMap<>(8);  // todo Уточнить сколько экспертов обычно оценивает одного человека
    criteriaScoresByParticipantId.put(participantId, new CriteriaScoreForParticipantDto(participant, scoreByExpertId));
  }


  final Set<CriteriaScoreShortProj> scores = scoreRepository.getAllByParticipant_IdInAndCriteria_Id(
      participantsById.keySet(), criteriaId, CriteriaScoreShortProj.class);
  for (final CriteriaScoreShortProj score : scores) {
    final Long participantId = score.getParticipant().getId();
    final Long expertId = score.getExpert().getId();
    final ScoreIdCommentDto scoreValue = new ScoreIdCommentDto(score);

    criteriaScoresByParticipantId.get(participantId).scoreByExpertId().put(expertId, scoreValue);
  }

  return new CriteriaScoresAllDto(criteriaScoresByParticipantId.sequencedValues(), experts, participantsTotal);
}

private List<UserSimpleProj> getAllExperts() {
  return userRepository.findByCredentials_RoleInOrderBySecondNameAscFirstNameAscThirdNameAsc(
      Set.of(UserRole.ADMIN, UserRole.EXPERT), Pageable.unpaged(), UserSimpleProj.class).toList();
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public CriteriaScore setScore(final Long criteriaId, final Long expertId, final Long participantId,
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

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void delete(final Long criteriaId, final Long expertId, final Long participantId) {
  scoreRepository.deleteByCriteria_IdAndExpert_IdAndParticipant_Id(criteriaId, expertId, participantId);
}
}
