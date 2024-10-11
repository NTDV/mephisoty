package ru.valkovets.mephisoty.application.lifecycle.ticker;

import lombok.RequiredArgsConstructor;
import org.jctools.maps.NonBlockingHashSet;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.manager.StageScoreManager;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("BoundedWildcard")
@RequiredArgsConstructor
@Service
public class StageScoreTicker implements Ticker {
private static final AtomicBoolean IS_EVALUATING = new AtomicBoolean(false);
private static final AtomicBoolean EVALUATED_EVER = new AtomicBoolean(false);

private static final int READ_BATCH_SIZE = 64;

private static volatile Set<Long> evaluationQueue = new NonBlockingHashSet<>();
private final StageScoreManager scoreManager;

public static void addForEvaluation(final long stageId) {
  evaluationQueue.add(stageId);
}

public static void addAllForEvaluation(final Collection<Long> stageIds) {
  evaluationQueue.addAll(stageIds);
}

@Override
public void tick() {
  evaluateStageScore();
}

public void evaluateStageScore() {
  if (IS_EVALUATING.get()) return;
  IS_EVALUATING.set(true);

  if (EVALUATED_EVER.getAndSet(true)) {
    final Set<Long> stages = flushEvaluationQueue();

    if (stages != null && !stages.isEmpty()) {
      scoreManager.evaluateStageScore(stages);
    } else {
      IS_EVALUATING.set(false);
      return;
    }

  } else {
    //scoreManager.evaluateStageScore(Init._STAGES_ID);
  }

  IS_EVALUATING.set(false);
}


private static Set<Long> flushEvaluationQueue() {
  if (evaluationQueue.isEmpty()) {
    return null;
  }

  final Set<Long> ret = evaluationQueue;
  evaluationQueue = new NonBlockingHashSet<>();
  return ret;
}
}