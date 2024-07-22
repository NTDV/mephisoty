package ru.valkovets.mephisoty.api.lazydata;

import jakarta.annotation.Nullable;

public record GetAllCriteriaScoreFilter(
    //Set<Long> experts,
    @Nullable String name
) {
}
