package ru.valkovets.mephisoty.db.projection.special.stage;

import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.projection.extended.IdTdrseCommentProj;

/**
 * Projection for {@link Stage}
 */
public interface StageShortProj extends IdTdrseCommentProj {
//IdTitleProj getSeason();

String getLiteral();

String getStageVisibility();

String getApplyVisibility();

String getScoreVisibility();

String getScheduleAccessState();
}