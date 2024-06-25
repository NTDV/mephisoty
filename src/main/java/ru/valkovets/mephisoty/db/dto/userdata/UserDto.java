package ru.valkovets.mephisoty.db.dto.userdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.ParticipantState;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDto(@NotNull @Length(max = 200) String comment,
                      @NotNull ParticipantState state,
                      @NotBlank @Length(max = 50) String firstName,
                      @NotBlank @Length(max = 50) String secondName,
                      @NotNull @Length(max = 30) String thirdName)
        implements Serializable {
}