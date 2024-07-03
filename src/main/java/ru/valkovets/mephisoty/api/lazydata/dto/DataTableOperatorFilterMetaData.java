package ru.valkovets.mephisoty.api.lazydata.dto;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.api.lazydata.OperatorMode;

import java.util.Set;

public record DataTableOperatorFilterMetaData(
        @Nullable OperatorMode operator,
        @Nullable Set<DataTableFilterMetaData> constraints
) {}
