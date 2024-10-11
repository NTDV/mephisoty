package ru.valkovets.mephisoty.db.projection.special.stagescore;

import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link StageScore}
 */
public interface StageScoreShortProj extends IdCommentProj {
IdProj getParticipant();

IdProj getStage();

Float getScoreByStageFormula();
}