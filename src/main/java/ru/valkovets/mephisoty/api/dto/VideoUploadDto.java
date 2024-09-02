package ru.valkovets.mephisoty.api.dto;

import jakarta.annotation.Nullable;

public record VideoUploadDto(@Nullable Long fileId,
                             @Nullable String url) {
}
