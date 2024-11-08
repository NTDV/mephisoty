package ru.valkovets.mephisoty.api.dto;

import jakarta.annotation.Nullable;

public record TitleCaptainApplyDto(
    @Nullable Long id,
    String fullName,
    String groupTitle,

    String tgNick,
    String vkNick,
    String phoneNumber,

    @Nullable String title,
    @Nullable String captain
) {
}
