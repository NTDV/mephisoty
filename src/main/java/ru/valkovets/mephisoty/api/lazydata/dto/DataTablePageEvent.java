package ru.valkovets.mephisoty.api.lazydata.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Range;
import ru.valkovets.mephisoty.api.lazydata.DataTableFilterMetaDeserializer;
import ru.valkovets.mephisoty.api.lazydata.MatchMode;

import java.util.List;
import java.util.Map;

public record DataTablePageEvent(
        @NotNull @PositiveOrZero Long first,
        @NotNull @Positive Integer rows,
        @Nullable String sortField,
        @Nullable @Range(min = -1, max = 1) Integer sortOrder,
        @Nullable List<DataTableSortMeta> multiSortMeta,
        @Nullable @JsonDeserialize(contentUsing = DataTableFilterMetaDeserializer.class) Map<String, Object> filters,
        @Nullable MatchMode filterMatchModes,
        @Nullable @PositiveOrZero Integer page,
        @Nullable @Positive Integer pageCount
) {
}
