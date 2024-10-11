package ru.valkovets.mephisoty.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class GetAllDto<T> {
private final @NotNull Iterable<T> collection;
private final @PositiveOrZero long total;

private GetAllDto() {
    throw new NotImplementedException("Can not create instance of GetAllDto without args.");
}

public GetAllDto(final @NotNull Page<T> page) {
    collection = page.getContent();
    total = page.getTotalElements();
}

public static <T> GetAllDto<T> from(final @NotNull Page<T> page) {
    return new GetAllDto<>(page);
}
}
