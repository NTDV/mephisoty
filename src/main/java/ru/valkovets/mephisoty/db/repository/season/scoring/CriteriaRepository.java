package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface CriteriaRepository extends BasicRepository<Criteria> {
@EntityGraph("criteria_full")
<T> T getById(Long id, final Class<T> type);

}