package ru.valkovets.mephisoty.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.service.season.scoring.CriteriaScoreService;

@RestController
@RequestMapping("/admin/criteriascore")
@RequiredArgsConstructor
@Tag(name = "Оценки по критерию")
public class CriteriaScoreController {
private final CriteriaScoreService scoreService;

@PostMapping("/{criteriaId}")
@Operation(summary = "Получить информацию об оценках экспертов по критерию")
public GetAllDto<CriteriaScoreShortProj> getAll(@PathVariable final Long criteriaId,
                                                @RequestBody final DataTablePageEvent searchParams) {
  // todo Make here table (participant + group) x expert
  return GetAllDto.from(
      scoreService.getAll(
          (searchParams.page() == null
           ? (int) (searchParams.first() / searchParams.rows())
           : searchParams.page()),
          searchParams.rows(),
          (criteriaId != null && criteriaId != 0
           ? Specification.allOf(
              (root, query, builder) ->
                  builder.equal(root.get("criteria").get("id"), criteriaId),
              PageableService.parseFilter(searchParams))
           : PageableService.parseFilter(searchParams)),
          SortService.getSort(searchParams)));
}
}
