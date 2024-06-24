package ru.valkovets.mephisoty.db.repository.season.scoring.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

}