package ru.valkovets.mephisoty.db.service.season;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaDto;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.special.StageShortProj;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StageService {
private final StageRepository stageRepository;
private final CriteriaRepository criteriaRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<StageShortProj> getAll(final int page, final int size, final Specification<Stage> specification,
                                   final Sort sort) {
    return stageRepository.findBy(Specification.where(specification),
                                  q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                        .as(StageShortProj.class)
                                        .page(PageRequest.of(page, size)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Stage edit(@NotNull @Positive final Long id, final StageDto dto) {
    return stageRepository.save(getById(id).editFrom(dto));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Stage getById(@NotNull @Positive final Long id) {
    return stageRepository.findById(id).orElseThrow();
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void delete(final Long id) {
    stageRepository.deleteById(id);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<Criteria> getCriteriasFrom(final Long id) {
    return stageRepository.getCriteriasFrom(id, Criteria.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Stage addCriteriaFor(final Long id, final CriteriaDto criteriaDto) {
    final Stage stage = stageRepository.getById(id);
    stage.getCriterias().add(criteriaRepository.save(Criteria.from(criteriaDto, stage)));
    return stageRepository.save(stage);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<StageScore> getStageScoresFrom(final Long id) {
    return stageRepository.getStageScoresFrom(id, StageScore.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<StageSchedule> getStageScheduleFrom(final Long id) {
    return stageRepository.getStageScheduleFrom(id, StageSchedule.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<Question> getQuestionsFrom(final Long id) {
    return stageRepository.getQuestionsFrom(id, Question.class);
}
}
