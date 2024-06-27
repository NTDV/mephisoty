package ru.valkovets.mephisoty.api.dto;

import lombok.Builder;

@Builder
public record ErrorDto(String err, String dsc) {
    public static ErrorDto from(final Exception e) {
        return new ErrorDto(e.getClass().getSimpleName(), e.toString()); // todo remove dsc for release
    }
}
