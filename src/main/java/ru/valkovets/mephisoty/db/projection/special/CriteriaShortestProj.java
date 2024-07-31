package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link Criteria}
 */
public interface CriteriaShortestProj extends IdTitleProj {
Float getMax();

Float getMin();
}