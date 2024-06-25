package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link ru.valkovets.mephisoty.db.model.season.Season}
 */
@Builder
public record SeasonDto(@NotNull @Length(max = 200) String comment,
                        @NotBlank @Length(max = 120) String title,
                        @NotNull @Length(max = 2000) String description,
                        @NotNull @Length(max = 2000) String rules,
                        @NotNull OffsetDateTime start,
                        @NotNull OffsetDateTime end,
                        @Pattern(regexp = ValidationConst.FORMULA_PATTERN) @Length(max = 1000) String seasonResultFormula,
                        @NotNull AllowState stageVisibility,
                        @NotNull AllowState scoreVisibility)
        implements Serializable {
}