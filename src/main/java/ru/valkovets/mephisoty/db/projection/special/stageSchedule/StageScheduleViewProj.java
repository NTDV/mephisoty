package ru.valkovets.mephisoty.db.projection.special.stageSchedule;

import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link StageSchedule}
 */
public interface StageScheduleViewProj extends CreatedModifiedProj, StageScheduleTableProj {
IdTitleProj getStage();
}