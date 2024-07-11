package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.projection.extended.IdTdrseCommentProj;

/**
 * Projection for {@link Season}
 */
public interface SeasonProj extends IdTdrseCommentProj {
String getSeasonResultFormula();

String getSeasonVisibility();

String getScoreVisibility();
}