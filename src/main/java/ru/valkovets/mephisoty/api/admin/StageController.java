package ru.valkovets.mephisoty.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import ru.valkovets.mephisoty.api.dto.GetAllDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.api.lazydata.dto.DataTablePageEvent;
import ru.valkovets.mephisoty.api.lazydata.service.PageableService;
import ru.valkovets.mephisoty.api.lazydata.service.SortService;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaDto;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.StageCrudTableProj;
import ru.valkovets.mephisoty.db.service.season.StageService;

import java.util.Set;

@RestController
@RequestMapping("/admin/stage")
@RequiredArgsConstructor
@Tag(name = "Этапы конкурса")
public class StageController {
private final StageService stageService;

@PostMapping("/{seasonId}")
@Operation(summary = "Получить информацию об этапах")
public GetAllDto<StageCrudTableProj> getAll(@PathVariable final Long seasonId,
                                            @RequestBody final DataTablePageEvent searchParams) {
    return GetAllDto.from(
            stageService.getAll(
                    searchParams.page() == null ? (int) (searchParams.first() / searchParams.rows()) : searchParams.page(),
                    searchParams.rows(),
                    (seasonId != null && seasonId != 0 ?
                     Specification.allOf((root, query, builder) -> builder.equal(root.get("season").get("id"), seasonId),
                                         PageableService.parseFilter(searchParams)) :
                     PageableService.parseFilter(searchParams)),
                    SortService.getSort(searchParams)));
}

@GetMapping("/{id}")
@Operation(summary = "Получить информацию об этапе")
public Stage get(@PathVariable final Long id) {
    return stageService.getById(id);
}

@PutMapping("/{id}")
@Operation(summary = "Редактировать этап")
public Stage edit(@PathVariable final Long id, @RequestBody final StageDto dto) {
    return stageService.edit(id, dto);
}

@DeleteMapping("/{id}")
@Operation(summary = "Удаление этапа")
public void delete(@PathVariable final Long id) {
    stageService.delete(id);
}

@GetMapping("/{id}/criterias")
@Operation(summary = "Получить информацию о критериях этапа")
public Set<Criteria> getCriterias(@PathVariable final Long id) {
    return stageService.getCriteriasFrom(id);
}

@PostMapping("/{id}/criterias")
@Operation(summary = "Создать критерий и добавить к этапу")
public Stage addCriteria(@PathVariable final Long id, @RequestBody final CriteriaDto criteriaDto) {
    return stageService.addCriteriaFor(id, criteriaDto);
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
