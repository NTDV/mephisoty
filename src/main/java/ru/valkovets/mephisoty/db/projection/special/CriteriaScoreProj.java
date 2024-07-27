package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleProj;

/**
 * Projection for {@link CriteriaScore}
 */
public interface CriteriaScoreProj extends IdCommentProj {
UserSimpleGroupProj getParticipant();

UserSimpleProj getExpert();

Float getScore();


}