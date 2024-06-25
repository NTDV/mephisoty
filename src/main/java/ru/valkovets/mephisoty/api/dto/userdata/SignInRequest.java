package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;

import java.io.Serializable;

/**
 * DTO for {@link Credentials}
 */
public record SignInRequest(
        @Email @NotBlank @Length(max = 100) String email,
        @NotBlank @Length(max = 1024) String password) implements Serializable {
    public UsernamePasswordAuthenticationToken getAuthToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}