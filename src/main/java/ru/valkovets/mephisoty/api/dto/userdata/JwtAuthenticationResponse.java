package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record JwtAuthenticationResponse(@NotBlank String token)
    implements Serializable {
}
