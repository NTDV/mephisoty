package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

import java.io.Serializable;
import java.util.SequencedCollection;

public record StageScoresAllDto(
    @NotNull SequencedCollection<ScoreForParticipantDto> scores,
    @NotNull SequencedCollection<IdTitleProj> stages,
    @NotNull @PositiveOrZero Long totalParticipants
) implements Serializable {
}
