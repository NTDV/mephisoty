package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;

@Repository
public interface CriteriaScoreRepository extends JpaRepository<CriteriaScore, Long> {

}