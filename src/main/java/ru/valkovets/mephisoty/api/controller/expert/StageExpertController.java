package ru.valkovets.mephisoty.api.controller.expert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.LazySelectDto;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.service.season.StageService;

@RestController
@RequestMapping("/api/expert/stage")
@RequiredArgsConstructor
@Tag(name = "Этапы сезона конкурса")
public class StageExpertController {
private final StageService stageService;

@PostMapping("/select")
@Operation(summary = "Получить краткую информацию о доступных этапах")
public GetAllDto<IdTitleProj> getAllAvailableForSelect(@RequestBody @Nullable final LazySelectDto searchParams) {
  if (searchParams == null) {
    return GetAllDto.from(
        stageService.getAllForSelect(0, 24));
  } else {
    return GetAllDto.from(
        stageService.getAllForSelect(searchParams.first(), searchParams.last() - searchParams.first()));
  }
}

}
