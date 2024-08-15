package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;

import java.io.Serializable;
import java.util.Map;

public record ScoreForParticipantDto(
    @NotNull UserSelectProj participant,
    @NotNull Map<Long, ScoreIdCommentDto> scoreById
) implements Serializable {
}
