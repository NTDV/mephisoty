package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.CriteriaDto;
import ru.valkovets.mephisoty.api.dto.season.CriteriaScoreDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.dto.LazySelectDto;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria_;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaFullProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaShortProj;
import ru.valkovets.mephisoty.db.service.season.scoring.CriteriaService;

@RestController
@RequestMapping("/admin/criteria")
@RequiredArgsConstructor
@Tag(name = "Критерии оценки этапа сезона конкурса")
public class CriteriaController {
private final CriteriaService criteriaService;

@PostMapping("/{stageId}")
@Operation(summary = "Получить информацию о критериях этапа")
public GetAllDto<CriteriaShortProj> getAll(@PathVariable final Long stageId,
                                           @RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      criteriaService.getAll(
          (searchParams.page() == null
           ? (int) (searchParams.first() / searchParams.rows())
           : searchParams.page()),
          searchParams.rows(),
          (stageId != null && stageId != 0
           ? Specification.allOf(
              (root, query, builder) ->
                  builder.equal(root.get(Criteria_.stage).get(Criteria_.id), stageId),
              PageableService.parseFilter(searchParams))
           : PageableService.parseFilter(searchParams)),
          SortService.getSort(searchParams)));
}

@GetMapping("/{criteriaId}")
@Operation(summary = "Получить информацию о критерии")
public CriteriaFullProj get(@PathVariable final Long criteriaId) {
  return criteriaService.getById(criteriaId);
}

@PutMapping("/{criteriaId}")
@Operation(summary = "Редактировать критерий")
public CriteriaShortProj edit(@PathVariable final Long criteriaId, @RequestBody final CriteriaDto dto) {
  return criteriaService.edit(criteriaId, dto);
}

@DeleteMapping("/{criteriaId}")
@Operation(summary = "Удалить критерий")
public void delete(@PathVariable final Long criteriaId) {
  criteriaService.delete(criteriaId);
}

@PutMapping("/{criteriaId}/stage/{stageId}")
@Operation(summary = "Прикрепить критерий к этапу")
public CriteriaFullProj bindStage(@PathVariable final Long criteriaId, @PathVariable final Long stageId) {
  return criteriaService.bind(criteriaId, stageId);
}

@PostMapping("/{criteriaId}/score")
@Operation(summary = "Создать оценку и добавить к критерию")
public CriteriaScoreProj createScore(@PathVariable final Long criteriaId,
                                     @RequestBody final CriteriaScoreDto criteriaDto) {
  return criteriaService.addScoreFor(criteriaId, criteriaDto);
}

@PostMapping("/select")
@Operation(summary = "Получить краткую информацию о критериях")
public GetAllDto<IdTitleProj> getAllForSelect(@RequestBody @Nullable final LazySelectDto searchParams) {
  if (searchParams == null) {
    return GetAllDto.from(
        criteriaService.getAllForSelect(0, 24));
  } else {
    return GetAllDto.from(
        criteriaService.getAllForSelect(searchParams.first(), searchParams.last() - searchParams.first()));
  }
}
}
