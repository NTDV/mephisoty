package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.simple.UserNameIdProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;

/**
 * Projection for {@link CriteriaScore}
 */
public interface CriteriaScoreProj extends IdCommentProj {
UserSelectProj getParticipant();

UserNameIdProj getExpert();

Float getScore();


}