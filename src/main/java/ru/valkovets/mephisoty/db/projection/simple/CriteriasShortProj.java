package ru.valkovets.mephisoty.db.projection.simple;

import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

import java.util.Set;

/**
 * Projection for {@link Criteria}
 */
public interface CriteriasShortProj {
Set<IdTitleProj> getCriterias();
}