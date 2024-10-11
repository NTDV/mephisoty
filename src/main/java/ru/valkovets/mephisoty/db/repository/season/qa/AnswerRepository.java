package ru.valkovets.mephisoty.db.repository.season.qa;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Set;

@Repository
public interface AnswerRepository extends BasicRepository<Answer> {
default String getStateForVideo(final Long userId) {
  final Set<Answer> answers = findAllByParticipant_IdAndQuestion_Id(userId, 1L);

  if (answers.isEmpty()) {
    return null;
  } else if (answers.size() > 1) {
    return "multiple";
  } else {
    return "sent";
  }
}

Set<Answer> findAllByParticipant_IdAndQuestion_Id(final Long participantId, final Long questionId);

default String getStateForMaths(final Long userId) {
  final Set<Answer> answers = findAllByParticipant_IdAndQuestion_Id(userId, Init._2024_MATHS_QUESTION_ID);

  if (answers.isEmpty()) {
    return null;
  } else if (answers.size() > 1) {
    return "multiple";
  } else {
    return answers.stream().findAny().orElseThrow().getRichAnswer();
  }
}

default String getStateForWww(final Long userId) {
  final Set<Answer> answers = findAllByParticipant_IdAndQuestion_Id(userId, Init._2024_WWW_QUESTION_ID);

  if (answers.isEmpty()) {
    return null;
  } else if (answers.size() > 1) {
    return "multiple";
  } else {
    return answers.stream().findAny().orElseThrow().getRichAnswer();
  }
}
}