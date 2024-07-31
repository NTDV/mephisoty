package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;

import java.io.Serializable;
import java.util.Map;

public record StageScoreForParticipantDto(
    @NotNull UserSimpleGroupProj participant,
    @NotNull Map<Long, ScoreIdCommentDto> scoreByStageId
) implements Serializable {
}
