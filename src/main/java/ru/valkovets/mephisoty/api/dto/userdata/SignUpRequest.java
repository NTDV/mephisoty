package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.settings.UserRole;

import java.io.Serializable;

/**
 * DTO for {@link Credentials}
 */
public record SignUpRequest(
        @NotNull @Length(max = 200) String comment,
        @Email @NotBlank @Length(max = 100) String email,
        @NotBlank @Length(max = 1024) String password,
        @NotNull UserRegistrationDto user,
        @NotNull UserRole role) implements Serializable {
}