package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;

import java.io.Serializable;

/**
 * DTO for {@link Achievement}
 */
public record AchievementDto(@NotNull Integer typeCode,
                             @NotNull @Length(max = 200) String comment,
                             @NotBlank @Length(max = 1500) String description,
                             @NotNull Float totalScore,
                             @NotNull @Length(max = 255) String criteriaTitle,
                             @NotNull @Length(max = 500) String typeTitle,
                             @NotNull @Length(max = 255) String levelTitle,
                             @NotNull @Length(max = 255) String statusTitle,
                             @NotNull @Length(max = 255) String thanksFrom)
    implements Serializable {
}