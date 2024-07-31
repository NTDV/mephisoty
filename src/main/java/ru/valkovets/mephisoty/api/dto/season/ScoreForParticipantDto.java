package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;

import java.io.Serializable;
import java.util.Map;

public record ScoreForParticipantDto(
    @NotNull UserSimpleGroupProj participant,
    @NotNull Map<Long, ScoreIdCommentDto> scoreById
) implements Serializable {
}
