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
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.SeasonFullProj;
import ru.valkovets.mephisoty.db.projection.special.SeasonProj;
import ru.valkovets.mephisoty.db.projection.special.SeasonStagesShortProj;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeasonService {
private final ProjectionFactory projectionFactory;
private final SeasonRepository seasonRepository;
private final StageRepository stageRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public SeasonFullProj getById(@NotNull @Positive final Long id) {
    return seasonRepository.getById(id, SeasonFullProj.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public SeasonProj save(final SeasonDto season) {
    return projectionFactory.createProjection(SeasonProj.class,
                                              seasonRepository.save(Season.createFrom(season)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public SeasonProj edit(@NotNull @Positive final Long id, final SeasonDto dto) {
    return projectionFactory.createProjection(SeasonProj.class,
                                              seasonRepository.save(seasonRepository.findById(id).orElseThrow()
                                                                                    .editFrom(dto)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public void delete(@NotNull @Positive final Long id) {
    seasonRepository.deleteById(id);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<Stage> getStagesFrom(final Long id) {
    return seasonRepository.getSeasonStagesById(id, Stage.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public SeasonStagesShortProj addStageFor(final Long id, final StageDto stageDto) {
    final Season season = seasonRepository.getById(id, Season.class);
    season.getStages().add(stageRepository.save(Stage.createFrom(stageDto, season)));
    return projectionFactory.createProjection(SeasonStagesShortProj.class, seasonRepository.save(season));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<SeasonScore> getSeasonScoresFrom(final Long id) {
    return seasonRepository.getSeasonScoresById(id, SeasonScore.class);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<SeasonProj> getAll(final int page, final int size, final Specification<Season> specification,
                               final Sort sort) {
    return seasonRepository.findBy(Specification.where(specification),
                                   q -> q.sortBy(sort == null ? Sort.unsorted() : sort)
                                         .as(SeasonProj.class)
                                         .page(PageRequest.of(page, size)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public List<IdTitleProj> getAllIdTitleProjSortedByAlphabet() {
    return seasonRepository.getAllByOrderByTitleAscIdAsc(IdTitleProj.class);
}
}
