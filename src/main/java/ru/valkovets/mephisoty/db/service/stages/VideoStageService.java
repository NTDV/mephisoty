package ru.valkovets.mephisoty.db.service.stages;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.UserFileDto;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.repository.season.qa.AnswerRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoStageService {
private static final Logger log = LoggerFactory.getLogger(VideoStageService.class);
private final UserRepository userRepository;
private final AnswerRepository answerRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public Page<UserFileDto> getAll(final int page, final int size,
                                final Specification<User> specification,
                                final Sort sort) {
  final Page<User> participants = userRepository.findAll(
      Specification.<User>where((root, query, builder) ->
                                    builder.equal(root.join(User_.chosenStages)
                                                      .get(Stage_.id),
                                                  Init._2024_VIDEO_STAGE_ID))
                   .and(specification),
      PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort),
      NamedEntityGraph.loading("user_table_proj"));

  final Map<Long, Answer> answerByParticipantId = participants
      .stream()
      .flatMap(user -> answerRepository.findAllByParticipant_IdAndQuestion_Id(user.getId(), 1L).stream())
      .collect(Collectors.toMap(
          answer -> answer.getParticipant().getId(),
          answer -> answer,
          (a1, a2) -> {
            log.error("Duplicate answers found: {} and {}", a1, a2);
            return null;
          }));

  return participants.map(user -> UserFileDto.from(user, answerByParticipantId.get(user.getId())));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void update(final Long answerId, final Long fileId, final String url) {
  final Answer answer = answerRepository.findById(answerId).orElseThrow();

  if (fileId != null) answer.setFiles(Set.of(File.builder().id(fileId).build()));
  if (url != null) answer.setShortAnswer(url);

  answerRepository.save(answer);
}
}