package ru.valkovets.mephisoty.db.service.season;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeasonService {
private final SeasonRepository seasonRepository;
private final StageRepository stageRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Season getById(@NotNull @Positive final Long id) {
    return seasonRepository.findById(id).orElseThrow();
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Season getByIdWithStages(@NotNull @Positive final Long id) {
    return seasonRepository.getById(id);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Season save(final SeasonDto season) {
    return save(Season.createFrom(season));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Season save(final Season season) {
    return seasonRepository.save(season);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Season edit(@NotNull @Positive final Long id, final SeasonDto dto) {
    return save(getById(id).editFrom(dto));
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
public Season addStageFor(final Long id, final StageDto stage) {
    final Season season = getByIdWithStages(id);
    season.getStages().add(stageRepository.save(Stage.createFrom(stage, season)));
    return save(season);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Season addStageFor(final Long id, final Stage stage) {
    final Season season = getByIdWithStages(id);
    stage.setSeason(season); // todo удалить обратную зависимость
    season.getStages().add(stage);
    return save(season);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Set<SeasonScore> getSeasonScoresFrom(final Long id) {
    return seasonRepository.getSeasonScoresById(id, SeasonScore.class);
}
}
