package ru.valkovets.mephisoty.api.controller.admin.stages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.userdata.UserScheduleRecordDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.stages.DictantStageService;
import ru.valkovets.mephisoty.db.service.userdata.UserService;

@RestController
@RequestMapping("/api/admin/stages/dictant")
@RequiredArgsConstructor
@Tag(name = "Диктант победы")
public class DictantStageController {
private final DictantStageService dictantStageService;
private final UserService userService;

@PostMapping("/")
@Operation(summary = "Получить информацию об участниках диктанта победы")
public GetAllDto<UserScheduleRecordDto> getAll(@RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      dictantStageService.getAll(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          PageableService.parseFilter(searchParams),
          SortService.getSort(searchParams)));
}

@GetMapping("/{userId}/{dateId}")
@Operation(summary = "Изменить дату участия")
public void update(@PathVariable final Long userId, @PathVariable final Long dateId) {
  userService.chooseDictantDate(userId, dateId);
}
}
