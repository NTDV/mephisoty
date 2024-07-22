package ru.valkovets.mephisoty.api.lazydata.service;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
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
                      case final String string -> {
                          final boolean isName = key.equals("name") || key.endsWith(".name");
                          if (!isName) {
                              yield PageableService.<EntityT>useMode(MatchMode.equals, key, string);
                          } else {
                              yield (Specification<EntityT>) (
                                  (root, query, builder) -> {
                                      final Path<?> prefixPath = getNestedPath(root, key);
                                      return builder.like(
                                          builder.lower(
                                              builder.concat(
                                                  prefixPath.get("second_name"),
                                                  builder.concat(
                                                      prefixPath.get("first_name"),
                                                      prefixPath.get("third_name")))),
                                          ("%" + string.toLowerCase() + "%"));
                                  });
                          }
                      }

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

public static <EntityT> Specification<EntityT> useMode(final MatchMode matchMode, final String param, final String... values) {
    return (root, query, builder) -> {
        final Path<?> prefixPath = getNestedPath(root, param);

        return switch (matchMode) {
            case equals -> builder.equal(prefixPath, values[0]);
            case notEquals -> builder.notEqual(prefixPath, values[0]);

            case contains -> builder.like(builder.lower((Expression<String>) prefixPath), "%" + values[0].toLowerCase() + "%");
            case notContains ->
                builder.notLike(builder.lower((Expression<String>) prefixPath), "%" + values[0].toLowerCase() + "%");
            case endsWith -> builder.like(builder.lower((Expression<String>) prefixPath), "%" + values[0].toLowerCase());
            case startsWith -> builder.like(builder.lower((Expression<String>) prefixPath), values[0].toLowerCase() + "%");
            // todo Make ?all? case insensitive

            case in -> builder.in(prefixPath).in((Object[]) values);

            case lt -> builder.lt((Expression<? extends Number>) prefixPath, tryParseNumber(values[0]));
            case lte -> builder.le((Expression<? extends Number>) prefixPath, tryParseNumber(values[0]));
            case gt -> builder.gt((Expression<? extends Number>) prefixPath, tryParseNumber(values[0]));
            case gte -> builder.ge((Expression<? extends Number>) prefixPath, tryParseNumber(values[0]));

            case dateIs -> builder.equal(prefixPath, OffsetDateTime.parse(values[0]));
            case dateIsNot -> builder.notEqual(prefixPath, OffsetDateTime.parse(values[0]));
            case dateBefore ->
                builder.lessThan((Expression<? extends OffsetDateTime>) prefixPath, OffsetDateTime.parse(values[0]));
            case dateAfter ->
                builder.greaterThan((Expression<? extends OffsetDateTime>) prefixPath, OffsetDateTime.parse(values[0]));

            case between -> throw new NotImplementedException("Between mode is not implemented yet.");
        };
    };
}

public static Path<?> getNestedPath(final Root<?> root, final String path) {
    final String[] paths = path.split("\\.", 5);
    Path<?> ret = root.get(paths[0]);
    for (int i = 1; i < paths.length; i++) ret = ret.get(paths[i]);
    return ret;
}

public static <T> Specification<T> unitSpecs(final OperatorMode operatorMode, final Stream<Specification<T>> specs) {
    return specs.reduce(operatorMode == OperatorMode.and ? Specification::and : Specification::or).orElse(null);

}
}
