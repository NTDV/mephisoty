package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link Criteria}
 */
public interface CriteriaFullProj extends CreatedModifiedProj, CriteriaScoresShortProj {
IdTitleProj getStage();
}