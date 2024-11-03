package ru.valkovets.mephisoty.db.service.stages;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.TitleCaptainApplyDto;
import ru.valkovets.mephisoty.api.dto.TitleCaptainDto;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WwwStageService {
private final ObjectMapper objectMapper;
private final UserRepository userRepository;


@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public Page<TitleCaptainApplyDto> getAll(final int page, final int size,
                                         final Specification<User> specification,
                                         final Sort sort) {
  final Page<User> participants = userRepository.findAll(
      Specification.<User>where((root, query, builder) ->
                                    builder.equal(root.join(User_.chosenStages)
                                                      .get(Stage_.id),
                                                  Init._2024_WWW_STAGE_ID))
                   .and(specification),
      PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort),
      NamedEntityGraph.loading("user_table_proj"));


  return participants
      .map(participant -> {
        final Answer answer = participant
            .getAnswers()
            .stream()
            .filter(ans -> Objects.equals(ans.getQuestion().getId(),
                                          Init._2024_WWW_QUESTION_ID))
            .findAny()
            .orElse(Answer.builder().participant(participant).richAnswer("{}").build());

        try {
          final TitleCaptainDto titleCaptainDto = objectMapper.readValue(answer.getRichAnswer(), TitleCaptainDto.class);
          return new TitleCaptainApplyDto(
              answer.getParticipant().getId(),

              answer.getParticipant().getFullName(),
              answer.getParticipant().tryGetGroupTitle(),

              answer.getParticipant().getTgNick(),
              answer.getParticipant().getVkNick(),
              answer.getParticipant().getPhoneNumber(),

              titleCaptainDto.title(),
              titleCaptainDto.captain()
          );
        } catch (final JsonProcessingException e) {
          log.error("Failed to parse rich answer for answer {}", answer, e);
          return null;
        }
      });
}
}