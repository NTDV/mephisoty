package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StageScoreRepository extends BasicRepository<StageScore> {

<T> Set<T> getAllByParticipant_IdInAndStage_IdIn(Collection<Long> participantIds, Collection<Long> stageIds, Class<T> type);

<T> Set<T> getAllByStage_Id(Long stageId, Class<T> type);

<T> Set<T> getAllByStage_IdIn(Collection<Long> stageIds, Class<T> type);

List<StageScore> getAllByParticipant_IdAndStage_IdIn(Long participantIds, Collection<Long> stageIds);

@EntityGraph("stageScoreWithStage")
List<StageScore> getAllByParticipant_Id(Long participantId);

Optional<StageScore> findByStage_IdAndParticipant_Id(Long stageId, Long participantId);

void deleteByStage_IdAndParticipant_Id(Long stageId, Long participantId);

@Query("select max(ss.initialScore) from StageScore ss where ss.stage.id = ?1")
Float getMaxInitialScoreByStage_Id(Long stageId);

@Query("select min(ss.initialScore) from StageScore ss where ss.initialScore > 0 and ss.stage.id = ?1")
Float getMinNotZeroInitialScoreByStage_Id(Long stageId);
}