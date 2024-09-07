package ru.valkovets.mephisoty.api.controller.admin.stages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.projection.special.user.UserTableProj;
import ru.valkovets.mephisoty.db.service.userdata.UserService;

@RestController
@RequestMapping("/api/admin/stages/participants")
@RequiredArgsConstructor
@Tag(name = "Выбранные этапы участников")
public class ParticipantsStageController {
private final UserService userService;

@PostMapping("/")
@Operation(summary = "Получить информацию об участниках конкурса")
public GetAllDto<UserTableProj> getAll(@RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      userService.getAllForStages(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          PageableService.parseFilter(searchParams),
          SortService.getSort(searchParams)));
}
}
