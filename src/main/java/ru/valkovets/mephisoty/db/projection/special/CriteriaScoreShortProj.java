package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link CriteriaScore}
 */
public interface CriteriaScoreShortProj extends IdCommentProj {
IdProj getParticipant();
IdProj getExpert();
Float getScore();
}