package ru.valkovets.mephisoty.api.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.CriteriaDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.dto.LazySelectDto;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaFullProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageFullProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageShortProj;
import ru.valkovets.mephisoty.db.projection.special.file.FileProj;
import ru.valkovets.mephisoty.db.service.season.StageService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/stage")
@RequiredArgsConstructor
@Tag(name = "Этапы сезона конкурса")
public class StageController {
private final StageService stageService;

@PostMapping("/{seasonId}")
@Operation(summary = "Получить информацию об этапах")
public GetAllDto<StageShortProj> getAll(@PathVariable final Long seasonId,
                                        @RequestBody final DataTablePageEvent searchParams) {
    return GetAllDto.from(
            stageService.getAll(
                    searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
                    searchParams.rows(),
                    (seasonId != null && seasonId != 0
                     ? Specification.allOf(
                            (root, query, builder) ->
                                builder.equal(root.get(Stage_.season).get(Stage_.id), seasonId),
                            PageableService.parseFilter(searchParams))
                     : PageableService.parseFilter(searchParams)),
                    SortService.getSort(searchParams)));
}

@GetMapping("/{id}")
@Operation(summary = "Получить информацию об этапе")
public StageFullProj get(@PathVariable final Long id) {
    return stageService.getById(id);
}

@PutMapping("/{id}")
@Operation(summary = "Редактировать этап")
public StageProj edit(@PathVariable final Long id, @RequestBody final StageDto dto) {
    return stageService.edit(id, dto);
}

@DeleteMapping("/{id}")
@Operation(summary = "Удаление этапа")
public void delete(@PathVariable final Long id) {
    stageService.delete(id);
}

@PutMapping("/{stageId}/season/{seasonId}")
@Operation(summary = "Прикрепить этап к сезону")
public StageFullProj bindStage(@PathVariable final Long stageId, @PathVariable final Long seasonId) {
    return stageService.bindStage(seasonId, stageId);
}

@PostMapping("/select")
@Operation(summary = "Получить краткую информацию о сезонах")
public GetAllDto<IdTitleProj> getAllForSelect(@RequestBody @Nullable final LazySelectDto searchParams) {
    if (searchParams == null) {
        return GetAllDto.from(
            stageService.getAllForSelect(0, 24));
    } else {
        return GetAllDto.from(
            stageService.getAllForSelect(searchParams.first(), searchParams.last() - searchParams.first()));
    }
}

@PostMapping("/{stageId}/criterias")
@Operation(summary = "Создать критерий и добавить к сезону")
public CriteriaFullProj createCriteria(@PathVariable final Long stageId, @RequestBody final CriteriaDto criteriaDto) {
    return stageService.addCriteriaFor(stageId, criteriaDto);
}

@PutMapping("/{stageId}/files/{fileId}")
@Operation(summary = "Добавить файл к этапу")
public void addFile(@PathVariable final Long stageId, @PathVariable final Long fileId) {
    stageService.addFile(stageId, fileId);
}

@DeleteMapping("/{stageId}/files/{fileId}")
@Operation(summary = "Удалить файл, принадлежащий этапу")
public void deleteFile(@PathVariable final Long stageId, @PathVariable final Long fileId) {
    stageService.deleteFile(stageId, fileId);
}


@GetMapping("/{id}/criterias")
@Operation(summary = "Получить информацию о критериях этапа")
public Set<Criteria> getCriterias(@PathVariable final Long id) {
    return stageService.getCriteriasFrom(id);
}


@GetMapping("/{id}/scores")
@Operation(summary = "Получить информацию об оценках этапа")
public Set<StageScore> getScores(@PathVariable final Long id) {
    return stageService.getStageScoresFrom(id);
}

@GetMapping("/{id}/schedules")
@Operation(summary = "Получить информацию о записях этапа")
public Set<StageSchedule> getSchedules(@PathVariable final Long id) {
    return stageService.getStageScheduleFrom(id);
}

@GetMapping("/{id}/questions")
@Operation(summary = "Получить информацию о вопросах этапа")
public Set<Question> getQuestions(@PathVariable final Long id) {
    return stageService.getQuestionsFrom(id);
}
}
