package ru.valkovets.mephisoty.db.projection.special.stagescore;

import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.special.user.UserSimpleTableProj;

/**
 * Projection for {@link StageScore}
 */
public interface StageScoreTableProj extends IdCommentProj {
UserSimpleTableProj getParticipant();

Float getInitialScore();

Float getScoreByStageFormula();

Long getPlace();
}

