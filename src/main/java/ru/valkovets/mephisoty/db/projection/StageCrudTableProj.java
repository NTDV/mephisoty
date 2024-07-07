package ru.valkovets.mephisoty.db.projection;

import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.settings.AllowState;

/**
 * Projection for {@link Stage}
 */
public interface StageCrudTableProj extends TdrseCommentProj {
//IdTitleProj getSeason();

String getLiteral();

AllowState getStageVisibility();

AllowState getScoreVisibility();

AllowState getScheduleAccessState();
}