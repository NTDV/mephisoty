package ru.valkovets.mephisoty.db.manager;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.valkovets.mephisoty.api.dto.season.ScoreForParticipantDto;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ParticipantsManager {
public static ParticipantsTableResult getParticipantsTableResult(final UserRepository userRepository,
                                                                 final int page, final int size,
                                                                 final Specification<User> participantsFilter,
                                                                 final Sort participantSort) {
  final Page<UserSimpleGroupProj> participantsPage =
      getParticipantsPage(userRepository, page, size, participantsFilter, participantSort);

  final LinkedHashMap<Long, UserSimpleGroupProj> participantsById =
      participantsPage.get().collect(Collectors.toMap(UserSimpleGroupProj::getId, e -> e, (u1, u2) -> u1, LinkedHashMap::new));
  final long participantsTotal = participantsPage.getTotalElements();
  final int participantsCount = participantsById.size();

  final LinkedHashMap<Long, ScoreForParticipantDto> scoresByParticipantId =
      new LinkedHashMap<>(participantsCount);
  for (final UserSimpleGroupProj participant : participantsById.sequencedValues()) { // Может на стримах быстрее будет, но мне так не кажется
    final Long participantId = participant.getId();
    final HashMap<Long, ScoreIdCommentDto> scoreByExpertId =
        new HashMap<>(8);  // todo Уточнить сколько экспертов обычно оценивает одного человека
    scoresByParticipantId.put(participantId, new ScoreForParticipantDto(participant, scoreByExpertId));
  }

  return new ParticipantsTableResult(participantsById, participantsTotal, scoresByParticipantId);
}

public static Page<UserSimpleGroupProj> getParticipantsPage(final UserRepository userRepository,
                                                            final int page, final int size,
                                                            final Specification<User> participantsFilter,
                                                            final Sort participantSort) {
  return userRepository.findBy(
      Specification.where(participantsFilter)
                   .and((root, query, builder) -> {
                     final Join<User, Credentials> credentials = root.join("credentials", JoinType.LEFT);
                     return builder.or(
                         builder.isNull(root.get("credentials")),
                         builder.equal(credentials.get("role"), UserRole.PARTICIPANT),
                         builder.equal(credentials.get("role"), UserRole.ADMIN));
                   }),
      q -> q.sortBy(participantSort == null ? Sort.unsorted() : participantSort)
            .as(UserSimpleGroupProj.class)
            .page(PageRequest.of(page, size)));
}

public record ParticipantsTableResult(LinkedHashMap<Long, UserSimpleGroupProj> participantsById,
                                      long participantsTotal,
                                      LinkedHashMap<Long, ScoreForParticipantDto> scoresByParticipantId) {}
}
