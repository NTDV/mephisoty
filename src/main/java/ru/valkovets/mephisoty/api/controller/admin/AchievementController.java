package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.CsvUploadDto;
import ru.valkovets.mephisoty.db.service.season.scoring.portfolio.AchievementService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/achievement")
@RequiredArgsConstructor
@Tag(name = "Управление достижениями")
public class AchievementController {
private final AchievementService achievementService;

@PostMapping(value = "/import/{stageId}", consumes = "multipart/form-data")
@Tag(name = "Добавление новых достижений")
public List<String> importNew(@PathVariable("stageId") final Long stageId, @ModelAttribute final CsvUploadDto file)
throws IOException {
  return achievementService.importNew(file, stageId);
}
}
