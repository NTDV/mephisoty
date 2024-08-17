package ru.valkovets.mephisoty.db.manager;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.AchievementScore;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.repository.UtilsRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.portfolio.AchievementScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.AchievementType;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AchievementScoreManager {
private static final int TYPES_CACHE_LENGTH = AchievementType.values().length;
private static final int FLUSH_BATCH_SIZE = 64;
private static final int READ_BATCH_SIZE = 64;
private final UserRepository userRepository;
private final AchievementScoreRepository achievementScoreRepository;
private final StageRepository stageRepository;
private final UtilsRepository utilsRepository;

@Transactional
public void evaluateExpertAddition(final Map<ScoreIdAchievementType, Float> expertValueByScoreIdType) {
  utilsRepository
      .findAllByIdIn(expertValueByScoreIdType.keySet().parallelStream()
                                             .map(scoreIdType -> scoreIdType.achievementScoreId)
                                             .toList(),
                     AchievementScore.class)
      .stream()
      .map(score -> {
        final ScoreIdAchievementType scoreIdType = new ScoreIdAchievementType(score.getId(), 0);
        boolean somethingChanged = false;

        for (int i = 0; i < TYPES_CACHE_LENGTH; i++) {
          scoreIdType.setType(i);

          final Float expertScore = expertValueByScoreIdType.get(scoreIdType);
          if (expertScore != null && expertScore != score.getExpert()[i]) {

            score.getExpert()[i] = expertScore;
            somethingChanged = true;
          }
        }

        return somethingChanged ? null : score;
      })
      .filter(Objects::nonNull)
      .forEach(achievementScoreRepository::save);
}


@Transactional
public void evaluateAchievementScore(final Set<UserIdStageId> users) {
  if (users == null || users.isEmpty()) return;

  final ArrayList<AchievementScore> scores =
      new ArrayList<>(Math.min(FLUSH_BATCH_SIZE + TYPES_CACHE_LENGTH, users.size()));

  for (final UserIdStageId user_stage : users) {
    final AchievementScore newScore = evaluateNewScoresFor(user_stage);
    flushScore(newScore, scores);
  }

  achievementScoreRepository.saveAll(scores);
}

private AchievementScore evaluateNewScoresFor(final UserIdStageId userIdStageId) {
  final User user = userRepository.findById(userIdStageId.userId).orElse(null);
  return evaluateNewScoresFor(user, userIdStageId.stageId);
}

private void flushScore(final AchievementScore newScore, final ArrayList<AchievementScore> scores) {
  if (newScore == null) return;
  scores.add(newScore);

  if (scores.size() >= FLUSH_BATCH_SIZE) {
    achievementScoreRepository.saveAll(scores);
    scores.clear();
  }
}

private AchievementScore evaluateNewScoresFor(final User user, final long stageId) {
  if (user == null) return null;

  final SumCount sumCount = new SumCount();

  user.getAchievements()
      .forEach(achievement -> {
        final int index = achievement.getTypeCode().ordinal();
        sumCount.sum[index] += achievement.getTotalScore();
        sumCount.count[index] += 1L;

        sumCount.min[index] = Float.isNaN(sumCount.min[index]) ? achievement.getTotalScore()
                                                               : Math.min(sumCount.min[index], achievement.getTotalScore());
        sumCount.max[index] = Math.max(sumCount.max[index], achievement.getTotalScore());


        sumCount.sum[0] += achievement.getTotalScore();
        sumCount.count[0] += 1L;

        sumCount.min[0] = Float.isNaN(sumCount.min[0]) ? achievement.getTotalScore()
                                                       : Math.min(sumCount.min[0], achievement.getTotalScore());
        sumCount.max[0] = Math.max(sumCount.max[0], achievement.getTotalScore());
      });

  final float[] mean = new float[TYPES_CACHE_LENGTH];
  for (int i = 0; i < TYPES_CACHE_LENGTH; i++) {

    if (sumCount.count[i] != 0) {
      mean[i] = sumCount.sum[i] / (float) sumCount.count[i];
    }

    if (Float.isNaN(sumCount.min[i])) sumCount.min[i] = 0;
  }

  final AchievementScore existingScore = achievementScoreRepository
      .findByParticipant_IdAndStage_Id(user.getId(), stageId)
      .orElse(AchievementScore.builder().participant(user)
                              .stage(Stage.builder().id(stageId).build())
                              .build());

  if (Arrays.equals(sumCount.sum, existingScore.getSum()) &&
      Arrays.equals(mean, existingScore.getMean()) &&
      Arrays.equals(sumCount.min, existingScore.getMin()) &&
      Arrays.equals(sumCount.max, existingScore.getMax())) {
    return null;
  }

  existingScore.setSum(sumCount.sum);
  existingScore.setMean(mean);
  existingScore.setMin(sumCount.min);
  existingScore.setMax(sumCount.max);

  return existingScore;
}

@Transactional
public void initAchievementScore() {
  final Collection<Stage> stages = stageRepository.findAll();
  if (stages.isEmpty()) {
    return;
  }

  final ArrayList<AchievementScore> scores = new ArrayList<>(FLUSH_BATCH_SIZE + TYPES_CACHE_LENGTH);

  Pageable userPageable = Pageable.ofSize(READ_BATCH_SIZE);
  Page<User> users;

  do {
    users = userRepository.findAll(userPageable);

    for (final User user : users) {
      for (final Stage stage : stages) {
        final AchievementScore newScore = evaluateNewScoresFor(user, stage.getId());
        flushScore(newScore, scores);
      }
    }

    userPageable = users.nextPageable();
  } while (users.hasNext());

  achievementScoreRepository.saveAll(scores);
}

public record UserIdStageId(long userId, long stageId) {}

@Data
@AllArgsConstructor
public static class ScoreIdAchievementType {
  private long achievementScoreId;
  private int type;
}

private record SumCount(float[] sum, long[] count, float[] min, float[] max) {
  SumCount() {
    this(new float[TYPES_CACHE_LENGTH], new long[TYPES_CACHE_LENGTH],
         new float[] { Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN, Float.NaN },
         new float[TYPES_CACHE_LENGTH]);
  }
}
}