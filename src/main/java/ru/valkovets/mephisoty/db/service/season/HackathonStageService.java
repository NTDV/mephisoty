package ru.valkovets.mephisoty.db.service.season;

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
import ru.valkovets.mephisoty.api.dto.season.HackathonApplyDto;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.db.model.season.qa.Answer_;
import ru.valkovets.mephisoty.db.model.season.qa.Question_;
import ru.valkovets.mephisoty.db.repository.season.qa.AnswerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HackathonStageService {
private final AnswerRepository answerRepository;
private final ObjectMapper objectMapper;


@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public Page<HackathonApplyDto> getAll(final int page, final int size,
                                      final Sort sort) {
  return answerRepository
      .findAll(
          Specification.where((root, query, builder) ->
                                  builder.equal(root.join(Answer_.question).get(Question_.id),
                                                Init._2024_HACKATHON_QUESTION_ID)),
          PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort))
      .map(answer -> {
        try {
          return objectMapper.readValue(answer.getRichAnswer(), HackathonApplyDto.class);
        } catch (final JsonProcessingException e) {
          log.error("Failed to parse rich answer for answer {}", answer, e);
          return null;
        }
      });
}
}