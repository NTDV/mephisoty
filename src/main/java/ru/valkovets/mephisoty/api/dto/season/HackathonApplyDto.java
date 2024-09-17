package ru.valkovets.mephisoty.api.dto.season;

import jakarta.annotation.Nullable;

public record HackathonApplyDto(
    @Nullable Long userId,
    String firstName,
    String secondName,
    String thirdName,

    String groupTitle,
    String filial,

    String tg,
    String email,

    String task,
    String commandTitle
) {
}
