package ru.valkovets.mephisoty.api.controller.admin.stages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.HackathonApplyDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.season.HackathonStageService;

@RestController
@RequestMapping("/api/admin/stages/hackathon")
@RequiredArgsConstructor
@Tag(name = "Хакатон")
public class HackathonStageController {
private final HackathonStageService hackathonStageService;

@PostMapping("/")
@Operation(summary = "Получить информацию об участниках хакатона")
public GetAllDto<HackathonApplyDto> getAll(@RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      hackathonStageService.getAll(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          SortService.getSort(searchParams)));
}
}
