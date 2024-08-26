package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.dto.LazySelectDto;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.SeasonFullProj;
import ru.valkovets.mephisoty.db.projection.special.SeasonProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageFullProj;
import ru.valkovets.mephisoty.db.service.season.SeasonService;

import java.util.Set;

@RestController
@RequestMapping("/admin/season")
@RequiredArgsConstructor
@Tag(name = "Сезоны конкурса")
public class SeasonController {
private final SeasonService seasonService;

@PostMapping("/")
@Operation(summary = "Получить информацию о сезонах")
public GetAllDto<SeasonProj> getAll(@RequestBody final DataTablePageEvent searchParams) {
    return GetAllDto.from(
            seasonService.getAll(
                    searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
                    searchParams.rows(),
                    PageableService.parseFilter(searchParams),
                    SortService.getSort(searchParams)));
}

@PostMapping("/select")
@Operation(summary = "Получить краткую информацию о сезонах")
public GetAllDto<IdTitleProj> getAllForSelect(@RequestBody @Nullable final LazySelectDto searchParams) {
    if (searchParams == null) {
        return GetAllDto.from(
            seasonService.getAllForSelect(0, 24));
    } else {
        return GetAllDto.from(
            seasonService.getAllForSelect(searchParams.first(), searchParams.last() - searchParams.first()));
    }
}

@PostMapping
@Operation(summary = "Добавить сезон")
public SeasonProj create(@RequestBody final SeasonDto dto) {
    return seasonService.save(dto);
}

@GetMapping("/{id}")
@Operation(summary = "Получить информацию о сезоне")
public SeasonFullProj get(@PathVariable final Long id) {
    return seasonService.getById(id);
}

@PutMapping("/{id}")
@Operation(summary = "Редактировать сезон")
public SeasonProj edit(@PathVariable final Long id, @RequestBody final SeasonDto dto) {
    return seasonService.edit(id, dto);
}

@DeleteMapping("/{id}")
@Operation(summary = "Удаление сезона")
public void delete(@PathVariable final Long id) {
    seasonService.delete(id);
}

@GetMapping("/{id}/stages")
@Operation(summary = "Получить информацию об этапах сезона")
public Set<IdTitleProj> getStagesSelect(@PathVariable final Long id) {
    return seasonService.getStagesFrom(id);
}

@PostMapping("/{id}/stages")
@Operation(summary = "Создать этап и добавить к сезону")
public StageFullProj createStage(@PathVariable final Long id, @RequestBody final StageDto stageDto) {
    return seasonService.addStageFor(id, stageDto);
}

@GetMapping("/{id}/scores")
@Operation(summary = "Получить информацию об оценках сезона")
public Set<SeasonScore> getScores(@PathVariable final Long id) {
    return seasonService.getSeasonScoresFrom(id);
}
}
