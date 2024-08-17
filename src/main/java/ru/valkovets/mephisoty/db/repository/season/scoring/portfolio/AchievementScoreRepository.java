package ru.valkovets.mephisoty.db.repository.season.scoring.portfolio;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.AchievementScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

@Repository
public interface AchievementScoreRepository extends BasicRepository<AchievementScore> {
Optional<AchievementScore> findByParticipant_IdAndStage_Id(Long participantId, Long stageId);
}