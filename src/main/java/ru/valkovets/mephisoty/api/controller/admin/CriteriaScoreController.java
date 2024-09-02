package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.season.CriteriaScoresAllDto;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.season.scoring.CriteriaScoreService;

@RestController
@RequestMapping("/api/admin/criteriascore")
@RequiredArgsConstructor
@Tag(name = "Оценки по критерию")
public class CriteriaScoreController {
private final CriteriaScoreService scoreService;

@PostMapping("/{criteriaId}")
@Operation(summary = "Получить информацию об оценках экспертов по критерию")
public CriteriaScoresAllDto getAll(@PathVariable final Long criteriaId,
                                   @RequestBody final DataTablePageEvent searchParams) {
  return scoreService.getAll(
      searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
      searchParams.rows(),
      criteriaId,
      PageableService.parseFilter(searchParams),
      SortService.getSortForScoreGetAll(searchParams));
}

@PostMapping("/{criteriaId}/{expertId}/{participantId}")
@Operation(summary = "Установить оценку")
public void setScore(@PathVariable final Long criteriaId,
                     @PathVariable final Long expertId,
                     @PathVariable final Long participantId,
                     @RequestBody final ScoreIdCommentDto score) {
  scoreService.setScore(criteriaId, expertId, participantId, score);
}

@DeleteMapping("/{criteriaId}/{expertId}/{participantId}")
@Operation(summary = "Удалить оценку")
public void delete(@PathVariable final Long criteriaId,
                   @PathVariable final Long expertId,
                   @PathVariable final Long participantId) {
  scoreService.delete(criteriaId, expertId, participantId);
}
}