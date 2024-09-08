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
import ru.valkovets.mephisoty.api.dto.season.StageScheduleDto;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.projection.special.stageSchedule.StageScheduleTableProj;
import ru.valkovets.mephisoty.db.projection.special.stageSchedule.StageScheduleViewProj;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.schedule.StageScheduleRepository;

@Service
@RequiredArgsConstructor
public class StageScheduleService {
private final StageScheduleRepository stageScheduleRepository;
private final ProjectionFactory projectionFactory;
private final StageRepository stageRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public @NotNull Page<StageScheduleTableProj> getAll(final int page, final int size,
                                                    final Specification<StageSchedule> specification, final Sort sort) {
  return stageScheduleRepository.findBy(Specification.where(specification),
                                        q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                              .as(StageScheduleTableProj.class)
                                              .page(PageRequest.of(page, size)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public StageScheduleViewProj getById(final @NotNull @Positive Long id) {
  return stageScheduleRepository.getById(id, StageScheduleViewProj.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public StageScheduleTableProj edit(final @NotNull @Positive Long id, final StageScheduleDto dto) {
  return projectionFactory.createProjection(StageScheduleTableProj.class,
                                            stageScheduleRepository.save(
                                                stageScheduleRepository.findById(id)
                                                                       .orElseThrow()
                                                                       .editFrom(dto)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void delete(final @NotNull @Positive Long id) {
  stageScheduleRepository.deleteById(id);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public StageScheduleViewProj bind(final @NotNull @Positive Long stageScheduleId, @NotNull @Positive final Long stageId) {
  final StageSchedule stageSchedule = stageScheduleRepository.getById(stageScheduleId, StageSchedule.class);
  final Stage newStage = stageRepository.findById(stageId).orElseThrow();
  stageSchedule.setStage(newStage);
  return projectionFactory.createProjection(StageScheduleViewProj.class, stageScheduleRepository.save(stageSchedule));
}


}