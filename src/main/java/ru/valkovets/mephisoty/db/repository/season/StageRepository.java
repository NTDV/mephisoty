package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface StageRepository extends BasicRepository<Stage> {
    
}