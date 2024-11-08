package ru.valkovets.mephisoty.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.season.HackathonApplyDto;
import ru.valkovets.mephisoty.db.projection.special.file.FileProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StagePublicProj;
import ru.valkovets.mephisoty.db.service.season.StageService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Публичная информация конкурса")
public class PublicController {
private final StageService stageService;

@GetMapping("/stages")
@Operation(summary = "Получить информацию об актуальных этапах")
public List<StagePublicProj> getAll() {
  return stageService.getAllPublic(1L);
}

@GetMapping("/stages/{stageId}/files")
@Operation(summary = "Получить файлы к этапу")
public List<FileProj> getStageFiles(@PathVariable final Long stageId) {
  return stageService.getFiles(stageId);
}

@PostMapping("/applyhackathon")
@Operation(summary = "Подать заявку на хакатон")
public void applyHackathon(@RequestBody final HackathonApplyDto hackathonApplyDto) throws JsonProcessingException {
  stageService.applyHackathon(hackathonApplyDto);
}
}
