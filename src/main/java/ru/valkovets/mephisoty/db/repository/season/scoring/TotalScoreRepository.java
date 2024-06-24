package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;

@Repository
public interface TotalScoreRepository extends JpaRepository<StageScore, Long> {

}