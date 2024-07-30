package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StageScoreRepository extends BasicRepository<StageScore> {

<T> Set<T> getAllByParticipant_IdInAndStage_IdIn(Collection<Long> participantIds, Collection<Long> stageIds, Class<T> type);

Optional<StageScore> findByStage_IdAndParticipant_Id(Long stageId, Long participantId);

void deleteByStage_IdAndParticipant_Id(Long stageId, Long participantId);
}