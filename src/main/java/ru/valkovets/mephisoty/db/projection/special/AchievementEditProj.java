package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;

/**
 * Projection for {@link Achievement}
 */
public interface AchievementEditProj extends AchievementTableProj, CreatedModifiedProj {
}