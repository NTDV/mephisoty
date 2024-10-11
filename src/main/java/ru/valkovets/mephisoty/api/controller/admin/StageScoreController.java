package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.projection.special.stagescore.StageScoreTableProj;
import ru.valkovets.mephisoty.db.service.season.scoring.StageScoreService;

@RestController
@RequestMapping("/api/admin/stagescore")
@RequiredArgsConstructor
@Tag(name = "Оценки за этап")
public class StageScoreController {
private final StageScoreService scoreService;

@PostMapping("/{stageId}")
@Operation(summary = "Получить информацию об оценке за этап")
public GetAllDto<StageScoreTableProj> getAll(@PathVariable final Long stageId,
                                             @RequestBody final DataTablePageEvent searchParams) {
  return GetAllDto.from(
      scoreService.getAll(
          searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
          searchParams.rows(),
          stageId,
          PageableService.parseFilter(searchParams),
          SortService.getSort(searchParams)));
}

@GetMapping("/{stageId}")
@Operation(summary = "Посчитать информацию об оценке за этап")
public void evaluate(@PathVariable final Long stageId) {
  scoreService.evaluateFor(stageId);
}

@PostMapping("/{stageId}/{participantId}")
@Operation(summary = "Установить оценку")
public void setScore(@PathVariable final Long stageId,
                     @PathVariable final Long participantId,
                     @RequestBody final ScoreIdCommentDto score) {
  scoreService.setScore(stageId, participantId, score);
}

@DeleteMapping("/{stageId}/{participantId}")
@Operation(summary = "Удалить оценку")
public void delete(@PathVariable final Long stageId,
                   @PathVariable final Long participantId) {
  scoreService.delete(stageId, participantId);
}
}