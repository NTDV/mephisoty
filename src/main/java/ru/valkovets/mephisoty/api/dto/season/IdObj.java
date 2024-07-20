package ru.valkovets.mephisoty.api.dto.season;

import jakarta.validation.constraints.NotNull;

public record IdObj(
    @NotNull Long Id
) {
}
