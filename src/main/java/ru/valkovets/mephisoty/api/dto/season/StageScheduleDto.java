package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.settings.AllowState;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for {@link ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule}
 */
public record StageScheduleDto(@NotNull @Length(max = 200) String comment,
                               //@NotBlank @Length(max = 120) String title,
                               @NotNull @Length(max = 2000) String description,
                               //@NotNull @Length(max = 2000) String rules,
                               @NotNull OffsetDateTime start,
                               @NotNull OffsetDateTime end,
                               @NotNull AllowState state,
                               @NotNull Integer participantsMax)
    implements Serializable {
}