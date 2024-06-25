package ru.valkovets.mephisoty.db.repository.season.scoring.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface AchievementRepository extends BasicRepository<Achievement> {

}