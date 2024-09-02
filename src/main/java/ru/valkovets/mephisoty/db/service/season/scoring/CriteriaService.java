package ru.valkovets.mephisoty.db.service.season.scoring;

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
import ru.valkovets.mephisoty.api.dto.season.CriteriaScoreDto;
import ru.valkovets.mephisoty.api.lazydata.OffsetBasedPageRequest;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaFullProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreProj;
import ru.valkovets.mephisoty.db.projection.special.CriteriaShortProj;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

@Service
@RequiredArgsConstructor
public class CriteriaService {
private final ProjectionFactory projectionFactory;
private final CriteriaRepository criteriaRepository;
private final StageRepository stageRepository;
private final UserRepository userRepository;
private final CriteriaScoreRepository scoreRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<CriteriaShortProj> getAll(final int page, final int size,
                                      final Specification<Criteria> specification, final Sort sort) {
  return criteriaRepository.findBy(Specification.where(specification),
                                   q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                         .as(CriteriaShortProj.class)
                                         .page(PageRequest.of(page, size)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public CriteriaFullProj getById(@NotNull @Positive final Long id) {
  return criteriaRepository.getById(id, CriteriaFullProj.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public CriteriaShortProj edit(@NotNull @Positive final Long id, final CriteriaDto dto) {
  return projectionFactory.createProjection(CriteriaShortProj.class,
                                            criteriaRepository.save(
                                                criteriaRepository.findById(id)
                                                                  .orElseThrow()
                                                                  .editFrom(dto)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void delete(@NotNull @Positive final Long id) {
  criteriaRepository.deleteById(id);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public CriteriaFullProj bind(final Long criteriaId, final Long stageId) {
  final Criteria criteria = criteriaRepository.getById(criteriaId, Criteria.class);
  final Stage oldStage = criteria.getStage();
  if (oldStage.getId().equals(stageId)) return projectionFactory.createProjection(CriteriaFullProj.class, criteria);

  final Stage newStage = stageRepository.getById(stageId, Stage.class);
  //oldStage.getCriterias().remove(criteria);
  criteria.setStage(newStage); // Достаточно, т.к. Criteria владеет Stage
  //newStage.getCriterias().add(criteria);

  //stageRepository.saveAll(List.of(oldStage, newStage));
  return projectionFactory.createProjection(CriteriaFullProj.class, criteriaRepository.save(criteria));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public CriteriaScoreProj addScoreFor(final Long criteriaId, final CriteriaScoreDto scoreDto) {
  final Criteria criteria = criteriaRepository.findById(criteriaId).orElseThrow();
  final User expert = scoreDto.expert() == null ?
                      Credentials.getCurrent().getUser() :
                      userRepository.findById(scoreDto.expert()).orElseThrow();
  final User participant = userRepository.findById(scoreDto.participant()).orElseThrow();
  //criteria.addScore(CriteriaScore.from(scoreDto));
  return projectionFactory.createProjection(
      CriteriaScoreProj.class,
      scoreRepository.save(
          CriteriaScore
              .builder()
              .comment(scoreDto.comment())
              .score(scoreDto.score())
              .criteria(criteria)
              .expert(expert)
              .participant(participant)
              .build()));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<IdTitleProj> getAllForSelect(final long offset, final long limit) {
  if (offset < 0 || limit < 1) return Page.empty();
  return criteriaRepository.getAllByOrderByTitleAscIdAsc(new OffsetBasedPageRequest(offset, limit), IdTitleProj.class);
}
}
