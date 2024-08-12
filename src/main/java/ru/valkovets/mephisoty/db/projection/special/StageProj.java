package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.projection.extended.IdTdrseCommentProj;

/**
 * Projection for {@link Stage}
 */
public interface StageProj extends IdTdrseCommentProj {
String getLiteral();
String getStageResultFormula();
String getStageVisibility();
String getScoreVisibility();
String getScheduleAccessState();
}