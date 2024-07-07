package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.Set;

@Repository
public interface StageRepository extends BasicRepository<Stage> {
@Query("select c from Criteria c where c.stage.id = ?1")
    //@Query("select  stage.criterias from Stage stage where stage.id = ?1")
<T> Set<T> getCriteriasFrom(Long id, final Class<T> type);

@EntityGraph("stage_with_criterias")
Stage getById(Long id);

@Query("select s from StageScore s where s.stage.id = ?1")
    //@Query("select stage.stageScores from Stage stage where stage.id = ?1")
<T> Set<T> getStageScoresFrom(Long id, Class<T> type);

@Query("select s from StageSchedule s where s.stage.id = ?1")
    //@Query("select stage.stageSchedules from Stage stage where stage.id = ?1")
<T> Set<T> getStageScheduleFrom(Long id, Class<T> type);

@Query("select q from Question q where q.stage.id = ?1")
    //@Query("select stage.questions from Stage stage where stage.id = ?1")
<T> Set<T> getQuestionsFrom(Long id, Class<T> type);
}