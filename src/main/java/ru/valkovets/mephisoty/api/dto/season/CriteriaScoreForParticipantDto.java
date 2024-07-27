package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;

import java.io.Serializable;
import java.util.Map;

public record CriteriaScoreForParticipantDto(
    @NotNull UserSimpleGroupProj participant,
    @NotNull Map<Long, Float> scoreByExpertId
) implements Serializable {
}
