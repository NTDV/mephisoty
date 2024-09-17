package ru.valkovets.mephisoty.api.controller.admin.stages;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.special.user.UserTableProj;
import ru.valkovets.mephisoty.db.service.userdata.UserService;
import ru.valkovets.mephisoty.settings.UserRole;

@RestController
@RequestMapping("/api/admin/stages/participants")
@RequiredArgsConstructor
@Tag(name = "Выбранные этапы участников")
public class ParticipantsStageController {
private final UserService userService;

@PostMapping("/{stageId}")
@Operation(summary = "Получить информацию об участниках этапов конкурса")
public GetAllDto<UserTableProj> getAll(@PathVariable final Long stageId,
                                       @RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      userService.getAllForStages(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          (stageId != null && stageId != 0
           ? Specification.allOf(
              (root, query, builder) ->
                  builder.and(
                      builder.equal(root.get(User_.credentials).get("role"), UserRole.PARTICIPANT),
                      builder.equal(root.join(User_.chosenStages).get(Stage_.id), stageId)),
              PageableService.parseFilter(searchParams))
           : PageableService.parseFilter(searchParams)),
          SortService.getSort(searchParams)));
}
}