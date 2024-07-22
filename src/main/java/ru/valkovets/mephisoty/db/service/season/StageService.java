package ru.valkovets.mephisoty.db.service.season;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaDto;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.special.StageFullProj;
import ru.valkovets.mephisoty.db.projection.special.StageProj;
import ru.valkovets.mephisoty.db.projection.special.StageShortProj;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StageService {
private final ProjectionFactory projectionFactory;
private final StageRepository stageRepository;
private final CriteriaRepository criteriaRepository;
private final SeasonRepository seasonRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<StageShortProj> getAll(final int page, final int size, final Specification<Stage> specification, final Sort sort) {
    return stageRepository.findBy(Specification.where(specification),
                                  q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                        .as(StageShortProj.class)
                                        .page(PageRequest.of(page, size)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public StageProj edit(@NotNull @Positive final Long id, final StageDto dto) {
    return projectionFactory.createProjection(StageProj.class,
                                              stageRepository.save(stageRepository.findById(id).orElseThrow()
                                                                                  .editFrom(dto)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public StageFullProj getById(@NotNull @Positive final Long id) {
    return stageRepository.getById(id, StageFullProj.class);
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

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public StageFullProj bindStage(final Long newSeasonId, final Long stageId) {
    final Stage stage = stageRepository.getById(stageId, Stage.class);
    final Season oldSeason = stage.getSeason();
    if (oldSeason.getId().equals(newSeasonId)) return projectionFactory.createProjection(StageFullProj.class, stage);

    final Season newSeason = seasonRepository.getById(newSeasonId, Season.class);
    oldSeason.getStages().remove(stage);
    stage.setSeason(newSeason);
    newSeason.getStages().add(stage);

    seasonRepository.save(oldSeason);
    seasonRepository.save(newSeason);
    return projectionFactory.createProjection(StageFullProj.class, stageRepository.save(stage));
}
}
