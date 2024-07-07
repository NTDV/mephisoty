package ru.valkovets.mephisoty.db.projection;

import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.settings.AllowState;

/**
 * Projection for {@link Season}
 */
public interface SeasonCrudTableProj extends TdrseCommentProj {
//IdTitleProj getSeason();

String getSeasonResultFormula();

AllowState getSeasonVisibility();

AllowState getScoreVisibility();
}