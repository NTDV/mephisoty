package ru.valkovets.mephisoty.api.controller.expert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.dto.season.StageCriteriasScoresAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.season.scoring.CriteriaScoreService;

@RestController
@RequestMapping("/api/expert/stagecriteriascore")
@RequiredArgsConstructor
@Tag(name = "Оценки эксперта по критериям этапа")
public class StageCriteriasScoreController {
private final CriteriaScoreService scoreService;

@PostMapping("/{stageId}")
@Operation(summary = "Получить информацию об оценках за критерии этапа")
public StageCriteriasScoresAllDto getAll(@PathVariable final Long stageId,
                                         @RequestBody final DataTablePageEvent searchParams) {
  return scoreService.getAllForStage(
      searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
      searchParams.rows(),
      stageId,
      PageableService.parseFilter(searchParams),
      SortService.getSortForScoreGetAll(searchParams));
}

@PostMapping("/{criteriaId}/{participantId}")
@Operation(summary = "Установить оценку")
public void setScore(@PathVariable final Long criteriaId,
                     @PathVariable final Long participantId,
                     @RequestBody final ScoreIdCommentDto score) {
  scoreService.setScore(criteriaId, participantId, score);
}

@DeleteMapping("/{criteriaId}/{participantId}")
@Operation(summary = "Удалить оценку")
public void delete(@PathVariable final Long criteriaId,
                   @PathVariable final Long participantId) {
  scoreService.delete(criteriaId, participantId);
}
}