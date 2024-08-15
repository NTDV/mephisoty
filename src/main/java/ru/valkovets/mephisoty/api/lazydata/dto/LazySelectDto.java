package ru.valkovets.mephisoty.api.lazydata.dto;

import jakarta.annotation.Nullable;

public record LazySelectDto (
    Long first,
    Long last,
    @Nullable String value
) {
}
