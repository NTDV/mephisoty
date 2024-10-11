package ru.valkovets.mephisoty.api.controller.admin.stages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.TitleCaptainApplyDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.stages.WwwStageService;

@RestController
@RequestMapping("/api/admin/stages/www")
@RequiredArgsConstructor
@Tag(name = "Мат бои")
public class WwwStageController {
private final WwwStageService stageService;

@PostMapping("/")
@Operation(summary = "Получить информацию об участниках чгк")
public GetAllDto<TitleCaptainApplyDto> getAll(@RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      stageService.getAll(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          PageableService.parseFilter(searchParams),
          SortService.getSort(searchParams)));
}
}