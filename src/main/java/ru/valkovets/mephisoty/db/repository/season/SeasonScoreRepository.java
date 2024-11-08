package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

public interface SeasonScoreRepository extends BasicRepository<SeasonScore> {
Optional<SeasonScore> findBySeason_IdAndParticipant_Id(Long seasonId, Long participantId);
Optional<SeasonScore> findTopBySeason_IdOrderByPlaceDesc(Long seasonId);
}