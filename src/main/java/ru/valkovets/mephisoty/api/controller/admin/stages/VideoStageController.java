package ru.valkovets.mephisoty.api.controller.admin.stages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.userdata.UserFileDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.stages.VideoStageService;

@RestController
@RequestMapping("/api/admin/stages/video")
@RequiredArgsConstructor
@Tag(name = "Конкурс видео визиток")
public class VideoStageController {
private final VideoStageService videoStageService;

@PostMapping("/")
@Operation(summary = "Получить информацию об участниках конкурса видео визиток")
public GetAllDto<UserFileDto> getAll(@RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      videoStageService.getAll(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          PageableService.parseFilter(searchParams),
          SortService.getSort(searchParams)));
}

@PostMapping("/{answerId}")
@Operation(summary = "Изменить видео визитку")
public void update(@PathVariable final Long answerId, @RequestBody final UserFileDto dto) {
  videoStageService.update(answerId, dto.fileId(), dto.url());
}
}
