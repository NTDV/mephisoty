package ru.valkovets.mephisoty.db.service.season.scoring;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.repository.season.scoring.CriteriaScoreRepository;

@Service
@RequiredArgsConstructor
public class CriteriaScoreService {
private final CriteriaScoreRepository scoreRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<CriteriaScoreShortProj> getAll(final int page, final int size, final Specification<CriteriaScore> specification,
                                           final Sort sort) {
  return scoreRepository.findBy(Specification.where(specification),
                                q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                      .as(CriteriaScoreShortProj.class)
                                      .page(PageRequest.of(page, size)));
}
}
