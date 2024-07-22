package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.projection.extended.IdTdrCommentProj;

/**
 * Projection for {@link Criteria}
 */
public interface CriteriaShortProj extends IdTdrCommentProj {
String getLiteral();

Float getMax();

Float getMin();
}