package ru.valkovets.mephisoty.application.lifecycle.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.valkovets.mephisoty.application.lifecycle.ticker.StageScoreTicker;

@RequiredArgsConstructor
@Component
public class StageScoreScheduler {
private final StageScoreTicker stageScoreTicker;

@Scheduled(fixedDelay = 1000 * 2)
public void tickEvaluation() {
  stageScoreTicker.tick();
}
}
