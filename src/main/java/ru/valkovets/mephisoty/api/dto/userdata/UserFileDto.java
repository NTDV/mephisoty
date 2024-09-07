package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.util.Set;

/**
 * DTO for {@link User}
 */
public record UserFileDto(
    @Nullable Long id,
    @Nullable String fullName,
    @Nullable String group,

    @Nullable Long answerId,
    @Nullable Long fileId,
    @Nullable String url) {
public static UserFileDto from(final User user, @Nullable final Answer answer) {
  final Group group = user.getGroup();
  final Set<File> files = answer == null ? null : answer.getFiles();

  return new UserFileDto(
      user.getId(),
      user.getFullName(),
      group == null ? null : group.getTitle(),

      answer == null ? null : answer.getId(),
      answer == null || files.isEmpty() ? null : files.iterator().next().getId(),
      answer == null ? null : answer.getShortAnswer());
}
}
