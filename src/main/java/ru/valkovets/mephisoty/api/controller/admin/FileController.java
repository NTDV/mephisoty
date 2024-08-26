package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.file.FileUploadDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.application.services.FileSystemStorageService;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.projection.special.file.FileFullProj;
import ru.valkovets.mephisoty.db.projection.special.file.FileProj;
import ru.valkovets.mephisoty.db.service.files.FileService;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@Tag(name = "Файлы системы")
public class FileController {
private static final Logger log = LoggerFactory.getLogger(FileController.class);
private final FileService fileService;
private final FileSystemStorageService fileSystemStorageService;
private final ProjectionFactory projectionFactory;

@PostMapping("/all")
@Operation(summary = "Получить информацию о файлах в системе")
public GetAllDto<FileFullProj> getAll(@RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      fileService.getAll(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          PageableService.parseFilter(searchParams),
          SortService.getSort(searchParams)));
}

@PostMapping(value = "/", consumes = "multipart/form-data")
@Tag(name = "Добавление нового файла")
public FileProj upload(@ModelAttribute final FileUploadDto file) throws IOException {
  return projectionFactory.createProjection(FileProj.class, fileSystemStorageService.createFromUpload(file));
}

@PostMapping("/{fileId}")
@Operation(summary = "Обновить информацию о файла")
public FileProj edit(@PathVariable final Long fileId, @ModelAttribute final FileUploadDto file) throws IOException {
  return projectionFactory.createProjection(FileProj.class, fileSystemStorageService.updateFromUpload(fileId, file));
}

@GetMapping(value = "/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Tag(name = "Получение файла")
public ResponseEntity<StreamingResponseBody> get(@PathVariable final Long fileId) {
  try {
    final File file = fileService.get(fileId);
    final Path path = file.tryGetByCurrentUser(fileSystemStorageService);

    if (path == null) {
      return ResponseEntity.notFound().build();

    } else {
      final StreamingResponseBody responseBody = outputStream -> Files.copy(path, outputStream);

      return ResponseEntity
          .ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"" + file.getOriginalName() + "\"")
          .body(responseBody);
    }

  } catch (final Exception e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.internalServerError().build();
  }
}

@GetMapping(value = "/public/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Tag(name = "Получение файла")
public ResponseEntity<StreamingResponseBody> getPublic(@PathVariable final Long fileId) {
  try {
    final File file = fileService.get(fileId);

    if (file.getAccessPolicy().equals(FileAccessPolicy.ALL)) {
      final Path path = fileSystemStorageService.load(String.valueOf(file.getId()));

      if (path == null) {
        return ResponseEntity.notFound().build();
      } else {
        final StreamingResponseBody responseBody = outputStream -> Files.copy(path, outputStream);

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getOriginalName() + "\"")
            .body(responseBody);
      }
    } else {
      return ResponseEntity.status(403).build();
    }
  } catch (final Exception e) {
    log.error(e.getMessage(), e);
    return ResponseEntity.internalServerError().build();
  }
}
}
