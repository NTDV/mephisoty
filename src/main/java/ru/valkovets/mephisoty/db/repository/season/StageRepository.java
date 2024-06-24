package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    
}