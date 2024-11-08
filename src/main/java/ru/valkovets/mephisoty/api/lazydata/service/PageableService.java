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
import ru.valkovets.mephisoty.db.model.userdata.User_;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PageableService {
private static final String SHADOW_SORT_BY_FULLNAME = "name";
private static final String SHADOW_SORT_BY_dot_FULLNAME = "." + SHADOW_SORT_BY_FULLNAME;
private static final String SHADOW_SORT_BY_ALL = "global";

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
              .filter(e -> e.getValue() != null && !SHADOW_SORT_BY_ALL.equals(e.getKey()))
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

@SuppressWarnings("unchecked")
public static <EntityT> Specification<EntityT> useMode(final MatchMode matchMode, final String param, final String... values) {
    return (root, query, builder) -> {
        final boolean isName = param.equals(SHADOW_SORT_BY_FULLNAME) || param.endsWith(SHADOW_SORT_BY_dot_FULLNAME);

        final String fixedParam;
        if (isName) {
            fixedParam = param.substring(0, Math.max(0, param.length() - 5));
        } else {
            fixedParam = param;
        }

        final Path<?> prefixPath = getNestedPath(root, fixedParam);
        if (isName) {
            return builder.like(
                builder.lower(prefixPath.get(User_.FULL_NAME)),
                ("%" + values[0].toLowerCase().replace(" ", "") + "%"));
        }
        //(string.toLowerCase() + "%"));

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
    if (path == null || path.isEmpty()) return root;

    final String[] paths = path.split("\\.", 4);
    Path<?> ret = root.get(paths[0]);
    for (int i = 1; i < paths.length; i++) ret = ret.get(paths[i]);
    return ret;
}

public static <T> Specification<T> unitSpecs(final OperatorMode operatorMode, final Stream<Specification<T>> specs) {
    return specs.reduce(operatorMode == OperatorMode.and ? Specification::and : Specification::or).orElse(null);

}
}
