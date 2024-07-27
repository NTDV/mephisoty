package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleProj;

import java.io.Serializable;
import java.util.SequencedCollection;

public record CriteriaScoresAllDto(
    //@NotNull Map<Long, ? extends Map<Long, Float>> scoreByExpertId_ByParticipantId,
    @NotNull SequencedCollection<CriteriaScoreForParticipantDto> scores,
    //@NotNull Map<Long, UserSimpleGroupProj> participants,
    @NotNull SequencedCollection<UserSimpleProj> experts,
    @NotNull @PositiveOrZero Long totalParticipants
) implements Serializable {
}
