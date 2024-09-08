package ru.valkovets.mephisoty.db.projection.special.stageSchedule;

import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.complex.SeProj;
import ru.valkovets.mephisoty.settings.AllowState;

/**
 * Projection for {@link StageSchedule}
 */
public interface StageScheduleTableProj extends IdCommentProj, SeProj {
String getDescription();

Integer getParticipantsMax();

AllowState getState();
}