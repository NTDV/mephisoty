package ru.valkovets.mephisoty.api.lazydata.dto;

import jakarta.annotation.Nullable;
import org.hibernate.validator.constraints.Range;

public record DataTableSortMeta(
        @Nullable String field,
        @Nullable @Range(min = -1, max = 1) Integer order
) {
}
