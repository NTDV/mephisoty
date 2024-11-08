package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Optional;

@Repository
public interface TotalScoreRepository extends BasicRepository<StageScore> {
  Optional<StageScore> findTopByStage_IdOrderByScoreByStageFormulaDesc(final Long stageId);
  Optional<StageScore> findTopByStage_IdOrderByPlaceDesc(final Long stageId);
}