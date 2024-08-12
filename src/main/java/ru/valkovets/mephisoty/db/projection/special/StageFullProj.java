package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link Stage}
 */
public interface StageFullProj extends CreatedModifiedProj, StageCriteriasShortProj {
  IdTitleProj getSeason();
}