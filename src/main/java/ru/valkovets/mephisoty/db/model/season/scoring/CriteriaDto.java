package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.io.Serializable;

/**
 * DTO for {@link Criteria}
 */
public record CriteriaDto(@NotNull @Length(max = 200) String comment,
                          @NotBlank @Length(max = 120) String title,
                          @NotNull @Length(max = 2000) String description,
                          @NotNull @Length(max = 2000) String rules,
                          @Pattern(regexp = ValidationConst.LITERAL_PATTERN) @NotBlank @Length(max = 100) String literal,
                          @NotNull Float max,
                          @NotNull Float min)
        implements Serializable {
}