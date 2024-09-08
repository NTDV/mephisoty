package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.StageScheduleDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule_;
import ru.valkovets.mephisoty.db.projection.special.stageSchedule.StageScheduleTableProj;
import ru.valkovets.mephisoty.db.projection.special.stageSchedule.StageScheduleViewProj;
import ru.valkovets.mephisoty.db.service.season.StageScheduleService;

@RestController
@RequestMapping("/api/admin/stageschedule")
@RequiredArgsConstructor
@Tag(name = "Расписание этапа сезона конкурса")
public class StageScheduleController {
private final StageScheduleService stageScheduleService;

@PostMapping("/{stageId}")
@Operation(summary = "Получить информацию о расписании этапа")
public GetAllDto<StageScheduleTableProj> getAll(@PathVariable final Long stageId,
                                                @RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      stageScheduleService.getAll(
          (searchParams.page() == null
           ? (int) (searchParams.first() / searchParams.rows())
           : searchParams.page()),
          searchParams.rows(),
          (stageId != null && stageId != 0
           ? Specification.allOf(
              (root, query, builder) ->
                  builder.equal(root.get(StageSchedule_.stage).get(Stage_.id), stageId),
              PageableService.parseFilter(searchParams))
           : PageableService.parseFilter(searchParams)),
          SortService.getSort(searchParams)));
}

@GetMapping("/{stageScheduleId}")
@Operation(summary = "Получить информацию о расписании")
public StageScheduleViewProj get(@PathVariable final Long stageScheduleId) {
  return stageScheduleService.getById(stageScheduleId);
}

@PutMapping("/{stageScheduleId}")
@Operation(summary = "Редактировать элемент расписания")
public StageScheduleTableProj edit(@PathVariable final Long stageScheduleId, @RequestBody final StageScheduleDto dto) {
  return stageScheduleService.edit(stageScheduleId, dto);
}

@DeleteMapping("/{stageScheduleId}")
@Operation(summary = "Удалить элемент расаписания")
public void delete(@PathVariable final Long stageScheduleId) {
  stageScheduleService.delete(stageScheduleId);
}

@PutMapping("/{stageScheduleId}/stage/{stageId}")
@Operation(summary = "Прикрепить элемент расписания к этапу")
public StageScheduleViewProj bindStage(@PathVariable final Long stageScheduleId, @PathVariable final Long stageId) {
  return stageScheduleService.bind(stageScheduleId, stageId);
}
}
