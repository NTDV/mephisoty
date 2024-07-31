package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.season.ScoreIdCommentDto;
import ru.valkovets.mephisoty.api.dto.season.StageScoresAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.service.season.scoring.StageScoreService;

@RestController
@RequestMapping("/admin/seasonstagescore")
@RequiredArgsConstructor
@Tag(name = "Оценки по этапам сезона")
public class SeasonStagesScoreController {
private final StageScoreService scoreService;

@PostMapping("/{seasonId}")
@Operation(summary = "Получить информацию об оценках за этапы сезона")
public StageScoresAllDto getAll(@PathVariable final Long seasonId,
                                @RequestBody final DataTablePageEvent searchParams) {
  return scoreService.getAll(
      searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
      searchParams.rows(),
      seasonId,
      PageableService.parseFilter(searchParams),
      SortService.getSortForScoreGetAll(searchParams));
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