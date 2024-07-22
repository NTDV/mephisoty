package ru.valkovets.mephisoty.api.dto.season;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;

import java.io.Serializable;

/**
 * DTO for {@link CriteriaScore}
 */
public record CriteriaScoreDto(@NotNull @Length(max = 200) String comment,
                               @NotNull @Positive Long participant,
                               @Nullable @Positive Long expert,
                               @NotNull @PositiveOrZero Float score)
    implements Serializable {
}