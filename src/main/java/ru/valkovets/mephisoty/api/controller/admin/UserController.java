package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
import ru.valkovets.mephisoty.api.lazydata.dto.LazySelectDto;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.model.userdata.Group_;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.AchievementTableProj;
import ru.valkovets.mephisoty.db.service.userdata.UserService;
import ru.valkovets.mephisoty.settings.ParticipantState;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "Пользователи системы")
public class UserController {
private final UserService userService;

@PostMapping("/select/participant")
@Operation(summary = "Получить список всех участников")
public GetAllDto<IdTitleProj> getAllParticipantsForSelect(@RequestBody @Nullable final LazySelectDto searchParams) {
  if (searchParams == null) {
    return GetAllDto.from(userService.getAllForSelect(
        (root, query, builder) -> builder.equal(root.get(User_.state), ParticipantState.PARTICIPANT.name()), 0, 24));
  } else {
    return GetAllDto.from(userService.getAllForSelect(
        (root, query, builder) -> {
          if (StringUtils.isBlank(searchParams.value())) {
            return builder.equal(root.get(User_.state), ParticipantState.PARTICIPANT.name());
          }

          final Join<User, Group> group = root.join(User_.group, JoinType.LEFT);
          return builder.and(
              builder.equal(root.get(User_.state), ParticipantState.PARTICIPANT.name()),
              builder.like(
                  builder.lower(
                      builder.function(
                          "replace", String.class,
                          builder.concat(builder.coalesce(group.get(Group_.title), builder.literal("")),
                                         root.get(User_.fullName)),
                          builder.literal(" "),
                          builder.literal(""))),
                  ("%" + searchParams.value().toLowerCase().replace(" ", "") + "%")));
        }, searchParams.first(), searchParams.last() - searchParams.first()));
  }
}

@PostMapping("/{participantId}/stage/{stageId}/achievement")
@Operation(summary = "Создать достижение участника")
public AchievementTableProj createAchievement(@PathVariable final Long participantId, @PathVariable final Long stageId,
                                              @RequestBody final AchievementDto dto) {
  return userService.createAchievement(participantId, stageId, dto);
}
}
