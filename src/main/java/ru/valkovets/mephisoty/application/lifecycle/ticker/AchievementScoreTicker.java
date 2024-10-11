package ru.valkovets.mephisoty.application.lifecycle.ticker;

import lombok.RequiredArgsConstructor;
import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashSet;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.manager.AchievementScoreManager;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("BoundedWildcard")
@RequiredArgsConstructor
@Service
public class AchievementScoreTicker implements Ticker {
private static final AtomicBoolean IS_EVALUATING = new AtomicBoolean(false);
private static final AtomicBoolean EVALUATED_EVER = new AtomicBoolean(false);
private static final int READ_BATCH_SIZE = 64;
private static volatile Set<AchievementScoreManager.UserIdStageId> evaluationQueue = new NonBlockingHashSet<>();
private static volatile Map<AchievementScoreManager.ScoreIdAchievementType, Float> expertQueue =
    new NonBlockingHashMap<>(READ_BATCH_SIZE / 2);
private final AchievementScoreManager scoreManager;

public static void addForExpertAddition(final long achievementScoreId, final int type, final float score) {
  expertQueue.put(new AchievementScoreManager.ScoreIdAchievementType(achievementScoreId, type), score);
}

public static void addForEvaluation(final long userId, final long stageId) {
  evaluationQueue.add(new AchievementScoreManager.UserIdStageId(userId, stageId));
}

public static void addAllForEvaluation(final Collection<User> users, final long stageId) {
  for (final User user : users) {
    evaluationQueue.add(new AchievementScoreManager.UserIdStageId(user.getId(), stageId));
  }
}

@Override
public void tick() {
  evaluateAchievementScore();
  evaluateExpertAddition();
}

public void evaluateAchievementScore() {
  if (IS_EVALUATING.get()) return;
  IS_EVALUATING.set(true);

  if (EVALUATED_EVER.getAndSet(true)) {
    final Set<AchievementScoreManager.UserIdStageId> users = flushEvaluationQueue();

    if (users != null && !users.isEmpty()) {
      scoreManager.evaluateAchievementScore(users);

    } else {
      IS_EVALUATING.set(false);
      return;
    }

  } else {
    scoreManager.initAchievementScore();
  }

  IS_EVALUATING.set(false);
}

public void evaluateExpertAddition() {
  if (IS_EVALUATING.get()) return;
  IS_EVALUATING.set(true);

  final Map<AchievementScoreManager.ScoreIdAchievementType, Float> expertValueByScoreIdType = flushExpertQueue();
  if (expertValueByScoreIdType != null && !expertValueByScoreIdType.isEmpty()) {
    scoreManager.evaluateExpertAddition(expertValueByScoreIdType);
  }

  IS_EVALUATING.set(false);
}

private static Set<AchievementScoreManager.UserIdStageId> flushEvaluationQueue() {
  if (evaluationQueue.isEmpty()) {
    return null;
  }

  final Set<AchievementScoreManager.UserIdStageId> ret = evaluationQueue;
  evaluationQueue = new NonBlockingHashSet<>();
  return ret;
}

private static Map<AchievementScoreManager.ScoreIdAchievementType, Float> flushExpertQueue() {
  if (expertQueue.isEmpty()) {
    return null;
  }

  final Map<AchievementScoreManager.ScoreIdAchievementType, Float> ret = expertQueue;
  expertQueue = new NonBlockingHashMap<>();
  return ret;
}
}