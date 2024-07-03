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
        return Sort.by(searchParams
                               .multiSortMeta()
                               .stream()
                               .filter(dataTableSortMeta -> dataTableSortMeta != null &&
                                                            dataTableSortMeta.field() != null)
                               .map((dataTableSortMeta -> new Sort.Order(getDirection(dataTableSortMeta.order()),
                                                                         dataTableSortMeta.field())))
                               .toList());
    } else if (searchParams.sortField() != null) {
        return Sort.by(getDirection(searchParams.sortOrder()), searchParams.sortField());
    } else {
        return Sort.unsorted();
    }
}
}
