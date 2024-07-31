package ru.valkovets.mephisoty.db.repository.season.scoring;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.List;

@Repository
public interface CriteriaRepository extends BasicRepository<Criteria> {
@EntityGraph("criteria_full")
<T> T getById(Long id, final Class<T> type);

<T> Page<T> getAllByOrderByTitleAscIdAsc(Pageable pageable, Class<T> type);

<T> List<T> getAllByStage_IdOrderByTitleAsc(Long stageId, Class<T> type);
}