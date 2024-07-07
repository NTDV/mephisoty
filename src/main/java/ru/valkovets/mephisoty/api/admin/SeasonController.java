package ru.valkovets.mephisoty.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.projection.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.SeasonCrudTableProj;
import ru.valkovets.mephisoty.db.service.season.SeasonService;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/season")
@RequiredArgsConstructor
@Tag(name = "Сезоны конкурса")
public class SeasonController {
private final ObjectMapper objectMapper;
private final SeasonService seasonService;

@GetMapping("/test")
public void test() {
    objectMapper.getRegisteredModuleIds().forEach(System.out::println);
}

@PostMapping("/")
@Operation(summary = "Получить информацию о сезонах")
public GetAllDto<SeasonCrudTableProj> getAll(@RequestBody final DataTablePageEvent searchParams) {
    return GetAllDto.from(
            seasonService.getAll(
                    searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
                    searchParams.rows(),
                    PageableService.parseFilter(searchParams),
                    SortService.getSort(searchParams)));
}

@GetMapping("/select")
@Operation(summary = "Получить краткую информацию о сезонах")
public List<IdTitleProj> getAllForSelect() {
    return seasonService.getAllIdTitleProjSortedByAlphabet();
}

@PostMapping
@Operation(summary = "Добавить сезон")
public Season create(@RequestBody final SeasonDto dto) {
    return seasonService.save(dto);
}

@GetMapping("/{id}")
@Operation(summary = "Получить информацию о сезоне")
public Season get(@PathVariable final Long id) {
    return seasonService.getById(id);
}

@PutMapping("/{id}")
@Operation(summary = "Редактировать сезон")
public Season edit(@PathVariable final Long id, @RequestBody final SeasonDto dto) {
    return seasonService.edit(id, dto);
}

@DeleteMapping("/{id}")
@Operation(summary = "Удаление сезона")
public void delete(@PathVariable final Long id) {
    seasonService.delete(id);
}

@GetMapping("/{id}/stages")
@Operation(summary = "Получить информацию об этапах сезона")
public Set<Stage> getStages(@PathVariable final Long id) {
    return seasonService.getStagesFrom(id);
}

@PostMapping("/{id}/stages")
@Operation(summary = "Создать этап и добавить к сезону")
public Season addStage(@PathVariable final Long id, @RequestBody final StageDto stageDto) {
    return seasonService.addStageFor(id, stageDto);
}

@GetMapping("/{id}/scores")
@Operation(summary = "Получить информацию об оценках сезона")
public Set<SeasonScore> getScores(@PathVariable final Long id) {
    return seasonService.getSeasonScoresFrom(id);
}
}
