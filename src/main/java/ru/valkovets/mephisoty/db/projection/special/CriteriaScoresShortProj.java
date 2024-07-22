package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;

import java.util.Set;

/**
 * Projection for {@link Criteria}
 */
public interface CriteriaScoresShortProj extends CriteriaShortProj {
Set<CriteriaScoreShortProj> getCriteriaScores();
}