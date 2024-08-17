package ru.valkovets.mephisoty.db.service.season.scoring.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.AchievementScoreDto;
import ru.valkovets.mephisoty.application.lifecycle.ticker.AchievementScoreTicker;
import ru.valkovets.mephisoty.db.repository.season.scoring.portfolio.AchievementScoreRepository;

@RequiredArgsConstructor
@Service
public class AchievementScoreService {
private final AchievementScoreRepository achievementScoreRepository;

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).EXPERT)")
public static void setExpertScoreFor(final Long achievementScoreId, final Integer achievementType,
                                     final Float expertScore) {
  AchievementScoreTicker.addForExpertAddition(achievementScoreId, achievementType, expertScore);
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).EXPERT)")
public AchievementScoreDto[] getFor(final Long stageId, final Long participantId) {
  return AchievementScoreDto.from(
      achievementScoreRepository.findByParticipant_IdAndStage_Id(participantId, stageId)
                                .orElseGet(() -> {
                                  AchievementScoreTicker.addForEvaluation(participantId, stageId);
                                  return null;
                                }));
}
}
