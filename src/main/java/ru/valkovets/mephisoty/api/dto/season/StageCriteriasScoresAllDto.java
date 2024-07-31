package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.valkovets.mephisoty.db.projection.special.CriteriaShortestProj;

import java.io.Serializable;
import java.util.SequencedCollection;

public record StageCriteriasScoresAllDto(
    @NotNull SequencedCollection<ScoreForParticipantDto> scores,
    @NotNull SequencedCollection<CriteriaShortestProj> criterias,
    @NotNull @PositiveOrZero Long totalParticipants
) implements Serializable {
}
