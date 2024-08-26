package ru.valkovets.mephisoty.api.dto.file;

import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;

public record FileUploadDto(
    @Nullable MultipartFile file,
    @Nullable String newFileName,
    @Nullable String code,
    @Nullable FileAccessPolicy accessPolicy
) {
}
