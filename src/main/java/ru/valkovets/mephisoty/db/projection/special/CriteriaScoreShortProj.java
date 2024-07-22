package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleProj;

/**
 * Projection for {@link CriteriaScore}
 */
public interface CriteriaScoreShortProj extends IdCommentProj {
UserSimpleProj getParticipant();

UserSimpleProj getExpert();

Float getScore();
}