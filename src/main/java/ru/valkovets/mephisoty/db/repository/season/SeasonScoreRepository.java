package ru.valkovets.mephisoty.db.repository.season;

import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

public interface SeasonScoreRepository extends BasicRepository<SeasonScore> {
Optional<SeasonScore> findBySeason_IdAndParticipant_Id(Long seasonId, Long participantId);
}