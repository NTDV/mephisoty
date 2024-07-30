package ru.valkovets.mephisoty.api.dto.season;

import ru.valkovets.mephisoty.db.projection.special.CriteriaScoreShortProj;
import ru.valkovets.mephisoty.db.projection.special.StageScoreShortProj;

public record ScoreIdCommentDto(Long id,
                                String comment,
                                Float score) {

public ScoreIdCommentDto(final CriteriaScoreShortProj scoreShortProj) {
  this(scoreShortProj.getId(),
       scoreShortProj.getComment() == null ? "" : scoreShortProj.getComment(),
       scoreShortProj.getScore());
}

public ScoreIdCommentDto(final StageScoreShortProj scoreShortProj) {
  this(scoreShortProj.getId(),
       scoreShortProj.getComment() == null ? "" : scoreShortProj.getComment(),
       scoreShortProj.getScore());
}
}
