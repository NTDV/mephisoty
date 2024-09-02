package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.season.AchievementScoreDto;
import ru.valkovets.mephisoty.db.service.season.scoring.portfolio.AchievementScoreService;

@RestController
@RequestMapping("/api/admin/achievementscore")
@RequiredArgsConstructor
@Tag(name = "Управление балами за достижения")
public class AchievementScoreController {
private final AchievementScoreService achievementScoreService;

@GetMapping("/{stageId}/{participantId}")
@Operation(summary = "Получить информацию о баллах за достижениях участника")
public AchievementScoreDto[] getFor(@PathVariable final Long stageId, @PathVariable final Long participantId) {
  return achievementScoreService.getFor(stageId, participantId);
}

@PostMapping("/{achievementScoreId}/{achievementType}")
@Operation(summary = "Назначить экспертный балл за достижения участника")
public void setExpertScoreFor(@PathVariable final Long achievementScoreId, @PathVariable final Integer achievementType,
                              @RequestBody final Float expertScore) {
  AchievementScoreService.setExpertScoreFor(achievementScoreId, achievementType, expertScore);
}
}
