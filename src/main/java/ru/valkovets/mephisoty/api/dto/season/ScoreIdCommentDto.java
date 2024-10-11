package ru.valkovets.mephisoty.api.dto.season;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.projection.special.StageCriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.projection.special.stagescore.StageScoreShortProj;

public record ScoreIdCommentDto(Long id,
                                String comment,
                                @Nullable Float initialScore,
                                Float score) {

public ScoreIdCommentDto(final CriteriaScoreShortProj scoreShortProj) {
  this(scoreShortProj.getId(),
       scoreShortProj.getComment() == null ? "" : scoreShortProj.getComment(),
       null,
       scoreShortProj.getScore());
}

public ScoreIdCommentDto(final StageScoreShortProj scoreShortProj) {
  this(scoreShortProj.getId(),
       scoreShortProj.getComment() == null ? "" : scoreShortProj.getComment(),
       null,
       scoreShortProj.getScoreByStageFormula());
}

public ScoreIdCommentDto(final StageCriteriaScoreShortProj score) {
  this(score.getId(),
       score.getComment() == null ? "" : score.getComment(),
       null,
       score.getScore());
}
}
