package ru.valkovets.mephisoty.api.controller.participant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.VideoUploadDto;
import ru.valkovets.mephisoty.api.dto.userdata.ParticipantMeDto;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.projection.special.stageSchedule.StageSchedulePublicProj;
import ru.valkovets.mephisoty.db.service.userdata.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/participant")
@RequiredArgsConstructor
@Tag(name = "Информация конкурса для участников")
public class ParticipantController {
private final UserService userService;

@GetMapping("/applyto/{stageId}")
@Operation(summary = "Записаться на этап")
public void applyToStage(@PathVariable final Long stageId) {
  userService.applyToStage(Credentials.getCurrent().getId(), stageId);
}

@GetMapping("/wirepark/dates")
@Operation(summary = "Доступные даты на Веревочный парк")
public Map<LocalDate, List<StageSchedulePublicProj>> getWireparkDates() {
  return userService.getWireparkDates();
}

@GetMapping("/wirepark/{dateId}")
@Operation(summary = "Выбрать дату на Веревочный парк")
public void chooseWireparkDate(@PathVariable final Long dateId) {
  userService.chooseWireparkDate(Credentials.getCurrent().getId(), dateId);
}

@GetMapping("/dictant/{dateId}")
@Operation(summary = "Выбрать дату на Диктант Победы")
public void chooseDictantDate(@PathVariable final Long dateId) {
  userService.chooseDictantDate(Credentials.getCurrent().getId(), dateId);
}

@PostMapping("/video/upload")
@Operation(summary = "Загрузить видео")
public void uploadVideo(@RequestBody final VideoUploadDto videoUploadDto) {
  userService.uploadVideo(Credentials.getCurrent().getId(), videoUploadDto);
}

@GetMapping("/me")
@Operation(summary = "Получить сводку о себе")
public ParticipantMeDto getMe() {
  return userService.getMeFor(Credentials.getCurrent().getId());
}

@PutMapping("/me")
@Operation(summary = "Обновить персональную информацию")
public void updateMe(@RequestBody final ParticipantMeDto participantMeDto) {
  userService.updateMeFor(Credentials.getCurrent().getId(), participantMeDto);
}
}