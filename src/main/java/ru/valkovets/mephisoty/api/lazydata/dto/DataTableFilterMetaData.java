package ru.valkovets.mephisoty.api.lazydata.dto;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.api.lazydata.MatchMode;

public record DataTableFilterMetaData(
        @Nullable String value,
        @Nullable MatchMode matchMode
) {}
