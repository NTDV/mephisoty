package ru.valkovets.mephisoty.api.lazydata.service;

import org.springframework.data.domain.Sort;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;

//@Service
public class SortService {
public static Sort.Direction getDirection(final Integer sortOrder) {
    return sortOrder == null || sortOrder == 1 ? Sort.Direction.ASC : Sort.Direction.DESC;
}

public static Sort getSort(final DataTablePageEvent searchParams) {
    if (searchParams.multiSortMeta() != null) {
        return Sort.by(
            searchParams
                .multiSortMeta()
                .stream()
                .filter(dataTableSortMeta -> dataTableSortMeta != null && dataTableSortMeta.field() != null)
                .map((dataTableSortMeta -> new Sort.Order(
                    getDirection(dataTableSortMeta.order()),
                    dataTableSortMeta.field().replace('.', '_'))))
                .toList());
    } else if (searchParams.sortField() != null) {
        return Sort.by(getDirection(searchParams.sortOrder()), searchParams.sortField());
    } else {
        return Sort.unsorted();
    }
}

public static Sort getSortForCriteriaScoreGetAll(final DataTablePageEvent searchParams) {
    if (searchParams.multiSortMeta() != null) {
        final String field = searchParams.multiSortMeta().getFirst().field();
        final Sort.Direction direction = getDirection(searchParams.multiSortMeta().getFirst().order());

        if ("name".equals(field)) {
            return Sort.by(
                new Sort.Order(direction, "secondName"),
                new Sort.Order(direction, "firstName"),
                new Sort.Order(direction, "thirdName"));
        }
    }
    return getSort(searchParams);
}
}
