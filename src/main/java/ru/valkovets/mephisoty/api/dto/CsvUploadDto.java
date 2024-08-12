package ru.valkovets.mephisoty.api.dto;

import io.micrometer.common.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record CsvUploadDto(MultipartFile file,
                           @Nullable String delimiter,
                           @Nullable Boolean hasHeader) {}
