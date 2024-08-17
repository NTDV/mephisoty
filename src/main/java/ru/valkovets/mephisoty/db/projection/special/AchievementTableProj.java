package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;

/**
 * Projection for {@link Achievement}
 */
public interface AchievementTableProj extends IdCommentProj {
Integer getTypeCode();

String getCriteriaTitle();

String getTypeTitle();

String getDescription();

String getLevelTitle();

String getStatusTitle();

String getThanksFrom();

Float getTotalScore();
}