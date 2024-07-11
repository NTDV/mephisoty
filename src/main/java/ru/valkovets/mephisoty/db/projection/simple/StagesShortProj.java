package ru.valkovets.mephisoty.db.projection.simple;

import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

import java.util.Set;

/**
 * Projection for {@link Season}
 */
public interface StagesShortProj {
Set<IdTitleProj> getStages();
}