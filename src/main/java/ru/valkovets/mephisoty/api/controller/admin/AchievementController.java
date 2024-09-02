package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.CsvUploadDto;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement_;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.special.AchievementTableProj;
import ru.valkovets.mephisoty.db.service.season.scoring.portfolio.AchievementService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/achievement")
@RequiredArgsConstructor
@Tag(name = "Управление достижениями")
public class AchievementController {
private final AchievementService achievementService;

@PostMapping("/{stageId}/{participantId}")
@Operation(summary = "Получить информацию о достижениях участника")
public GetAllDto<AchievementTableProj> getAll(@PathVariable final Long stageId, @PathVariable final Long participantId,
                                              @RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      achievementService.getAllFor(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          Specification.<Achievement>where((root, query, builder) ->
                                               builder.and(
                                                   builder.equal(root.get(Achievement_.owner).get(User_.id), participantId),
                                                   builder.equal(root.get(Achievement_.stage).get(Stage_.id), stageId)))
                       .and(PageableService.parseFilter(searchParams)),
          SortService.getSort(searchParams)));
}

@PostMapping(value = "/import/{stageId}", consumes = "multipart/form-data")
@Tag(name = "Добавление новых достижений")
public List<String> importNew(@PathVariable("stageId") final Long stageId, @ModelAttribute final CsvUploadDto file)
throws IOException {
  return achievementService.importNew(file, stageId);
}

@PutMapping("/{id}")
@Operation(summary = "Редактировать достижение")
public AchievementTableProj edit(@PathVariable final Long id, @RequestBody final AchievementDto dto) {
  return achievementService.edit(id, dto);
}

@DeleteMapping("/{id}")
@Operation(summary = "Удаление этапа")
public void delete(@PathVariable final Long id) {
  achievementService.delete(id);
}

}
