package ru.valkovets.mephisoty.api.lazydata.service;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.lazydata.MatchMode;
import ru.valkovets.mephisoty.api.lazydata.OperatorMode;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTableFilterMetaData;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTableOperatorFilterMetaData;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PageableService {
public static Number tryParseNumber(final String value) {
    if (value == null) return null;
    
    try {
        return Long.valueOf(value);
    } catch (final Exception ignored) {
        try {
            return Float.valueOf(value);
        } catch (final Exception e) {
            return null;
        }
    }
}

public static <EntityT> Specification<EntityT> useMode(final MatchMode matchMode, final String param, final String... values) {
    return (root, query, builder) -> switch (matchMode) {
        case equals -> builder.equal(root.get(param), values[0]);
        case notEquals -> builder.notEqual(root.get(param), values[0]);

        case contains -> builder.like(root.get(param), "%" + values[0] + "%");
        case notContains -> builder.notLike(root.get(param), "%" + values[0] + "%");
        case endsWith -> builder.like(root.get(param), "%" + values[0]);
        case startsWith -> builder.like(root.get(param), values[0] + "%");

        case in -> builder.in(root.get(param)).in((Object[]) values);

        case lt -> builder.lt(root.get(param), tryParseNumber(values[0]));
        case lte -> builder.le(root.get(param), tryParseNumber(values[0]));
        case gt -> builder.gt(root.get(param), tryParseNumber(values[0]));
        case gte -> builder.ge(root.get(param), tryParseNumber(values[0]));

        case dateIs -> builder.equal(root.get(param), OffsetDateTime.parse(values[0]));
        case dateIsNot -> builder.notEqual(root.get(param), OffsetDateTime.parse(values[0]));
        case dateBefore -> builder.lessThan(root.get(param), OffsetDateTime.parse(values[0]));
        case dateAfter -> builder.greaterThan(root.get(param), OffsetDateTime.parse(values[0]));

        case between -> throw new NotImplementedException("Between mode is not implemented yet.");

    };
}

public static <EntityT> Specification<EntityT> parseFilter(final DataTablePageEvent src) {
    if (src.filters() == null) return null;
    return src.filters()
              .entrySet()
              .stream()
              .filter(e -> e.getValue() != null && !"global".equals(e.getKey()))
              .map(e -> {
                  final String key = e.getKey();
                  final Object value = e.getValue();

                  return switch (value) {
                      case final String string -> PageableService.<EntityT>useMode(MatchMode.equals, key, string);

                      case final DataTableOperatorFilterMetaData operatorFilterMetaData -> unitSpecs(
                              operatorFilterMetaData.operator(),
                              Objects.requireNonNull(operatorFilterMetaData.constraints())
                                     .stream()
                                     .filter(filter -> filter.value() != null)
                                     .map(filter -> PageableService.<EntityT>useMode(filter.matchMode(), key,
                                                                                     filter.value())));

                      case final DataTableFilterMetaData filterMetaData ->
                              PageableService.<EntityT>useMode(filterMetaData.matchMode(), key, filterMetaData.value());

                      default -> throw new ClassCastException("Unsupported type: " + value.getClass() + ". " +
                                                              "Must be String, DataTableOperatorFilterMetaData or DataTableFilterMetaData.");
                  };
              })
              .filter(Objects::nonNull)
              .reduce(Specification::and)
              .orElse(null);
}

public static <T> Specification<T> unitSpecs(final OperatorMode operatorMode, final Stream<Specification<T>> specs) {
    return specs.reduce(operatorMode == OperatorMode.and ? Specification::and : Specification::or).orElse(null);

}
}
