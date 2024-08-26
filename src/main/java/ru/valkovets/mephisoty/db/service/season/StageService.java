package ru.valkovets.mephisoty.db.service.season;

import jakarta.persistence.EntityNotFoundException;
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
import ru.valkovets.mephisoty.api.dto.season.CriteriaDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.api.lazydata.OffsetBasedPageRequest;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaFullProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageFullProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StagePublicProj;
import ru.valkovets.mephisoty.db.projection.special.stage.StageShortProj;
import ru.valkovets.mephisoty.db.projection.special.file.FileProj;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaRepository;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.FileAccessPolicy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StageService {
private final ProjectionFactory projectionFactory;
private final StageRepository stageRepository;
private final CriteriaRepository criteriaRepository;
private final SeasonRepository seasonRepository;
private final FileRepository fileRepository;

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
public CriteriaFullProj addCriteriaFor(final Long stageId, final CriteriaDto criteriaDto) {
    final Stage stage = stageRepository.getById(stageId, Stage.class);
    final Criteria criteria = criteriaRepository.save(Criteria.from(criteriaDto, stage));
    stage.getCriterias().add(criteria);
    stageRepository.save(stage);
    return projectionFactory.createProjection(CriteriaFullProj.class, criteria);
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

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN," +
              "T(ru.valkovets.mephisoty.settings.UserRole).EXPERT)")
public Page<IdTitleProj> getAllForSelect(final long offset, final long limit) {
    return stageRepository.getAllByOrderByTitleAscIdAsc(new OffsetBasedPageRequest(offset, limit), IdTitleProj.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void addFile(final Long stageId, final Long fileId) {
  final boolean stageExists = stageRepository.existsById(stageId);
  if (!stageExists) throw new EntityNotFoundException("Stage with id " + stageId + " not found");

  final Stage stage = stageRepository.findById(stageId).orElseThrow();
  stage.addFile(fileRepository.findById(fileId).orElseThrow());

  stageRepository.save(stage);
}

public List<FileProj> getFiles(final Long stageId) {
  return stageRepository
      .findById(stageId)
      .orElseThrow()
      .getFiles()
      .parallelStream()
      .filter(File::canGetByCurrentUser)
      .map(file -> projectionFactory.createProjection(FileProj.class, file))
      .sorted(Comparator.comparing(FileProj::getCode).thenComparing(FileProj::getOriginalName))
      .toList();
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void deleteFile(final Long stageId, final Long fileId) {
  final Stage stage = stageRepository.findById(stageId).orElseThrow();
  stage.getFiles().remove(fileRepository.findById(fileId).orElseThrow());

  stageRepository.save(stage);
}

@Transactional
public List<StagePublicProj> getAllPublic(final Long i) {
  return stageRepository
      .getAllBySeason_IdOrderByIdAsc(i, Stage.class)
      .stream()
      .filter(stage -> AllowState.ALLOW_READ_FOR_PARTICIPANTS.name().equals(stage.getStageVisibility()))
      .map(stage -> {
        stage.setFiles(
            stage.getFiles()
                 .parallelStream()
                 .filter(file -> file.getAccessPolicy().equals(FileAccessPolicy.ALL))
                 .collect(Collectors.toUnmodifiableSet()));

        return projectionFactory.createProjection(StagePublicProj.class, stage);
      })
      .toList();
}
}
