package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;

/**
 * Projection for {@link Season}
 */
public interface SeasonFullProj extends CreatedModifiedProj, SeasonStagesShortProj {
}