package ru.valkovets.mephisoty.api.dto.season;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link Stage}
 */
public record StageDto(@NotNull @Length(max = 200) String comment,
                       @Nullable Long season,
                       @NotBlank @Length(max = 120) String title,
                       @NotNull @Length(max = 2000) String description,
                       @NotNull @Length(max = 2000) String rules,
                       @NotNull OffsetDateTime start,
                       @NotNull OffsetDateTime end,
                       @Pattern(regexp = ValidationConst.LITERAL_PATTERN) @Length(max = 100) String literal,
                       @Pattern(regexp = ValidationConst.FORMULA_PATTERN) @Length(max = 1000) String stageResultFormula,
                       @NotNull AllowState stageVisibility,
                       @NotNull AllowState applyVisibility,
                       @NotNull AllowState scoreVisibility,
                       @NotNull AllowState scheduleAccessState)
        implements Serializable {
}