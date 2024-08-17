package ru.valkovets.mephisoty.api.dto.season;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.AchievementScore;
import ru.valkovets.mephisoty.settings.AchievementType;

import java.io.Serializable;

/**
 * DTO for {@link AchievementScore}
 */
public record AchievementScoreDto(@NotNull Integer typeCode,
                                  @NotNull Long id,
                                  @NotNull Float sum,
                                  @NotNull Float mean,
                                  @Nullable Float formula,
                                  @Nullable Float expert,
                                  @Nullable Float min,
                                  @Nullable Float max)
    implements Serializable {
public static AchievementScoreDto[] from(final AchievementScore achievementScore) {
  if (achievementScore == null) {
    return new AchievementScoreDto[0];
  }

  final AchievementScoreDto[] score = new AchievementScoreDto[AchievementType._VALUES.length];
  for (int i = 0; i < score.length; i++) {

    score[i] = new AchievementScoreDto(i, achievementScore.getId(),
                                       achievementScore.getSum()[i],
                                       achievementScore.getMean()[i],
                                       achievementScore.getFormula()[i],
                                       achievementScore.getExpert()[i],
                                       achievementScore.getMin()[i],
                                       achievementScore.getMax()[i]);
  }
  return score;
}
}