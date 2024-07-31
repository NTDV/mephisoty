package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link CriteriaScore}
 */
public interface StageCriteriaScoreShortProj extends IdCommentProj {
IdProj getParticipant();

IdProj getCriteria();

Float getScore();
}