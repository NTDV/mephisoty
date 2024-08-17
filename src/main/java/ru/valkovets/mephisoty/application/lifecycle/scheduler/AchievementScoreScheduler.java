package ru.valkovets.mephisoty.application.lifecycle.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.valkovets.mephisoty.application.lifecycle.ticker.AchievementScoreTicker;

@RequiredArgsConstructor
@Component
public class AchievementScoreScheduler {
private final AchievementScoreTicker achievementScoreTicker;

@Scheduled(fixedDelay = 1000 * 2)
public void tickEvaluation() {
  achievementScoreTicker.tick();
}
}
