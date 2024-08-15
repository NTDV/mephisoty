package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;

import java.io.Serializable;
import java.util.Map;

public record StageScoreForParticipantDto(
    @NotNull UserSelectProj participant,
    @NotNull Map<Long, ScoreIdCommentDto> scoreByStageId
) implements Serializable {
}
