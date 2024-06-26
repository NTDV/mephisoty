package ru.valkovets.mephisoty.db.service.season;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;

@Service
@RequiredArgsConstructor
public class SeasonService {
private final SeasonRepository seasonRepository;

//@EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "season_with_stages")
public Season findById(@NotNull @Positive final Long id) {
    return seasonRepository.findById(id).orElseThrow();
}

public Season save(final SeasonDto season) {
    return seasonRepository.save(Season.createFrom(season));
}

public Season save(final Season season) {
    return seasonRepository.save(season);
}

public Season edit(final Long id, final SeasonDto dto) {
    return findById(id).editFrom(dto);
}

public void delete(final Long id) {
    seasonRepository.deleteById(id);
}
}
