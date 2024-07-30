package ru.valkovets.mephisoty.db.repository.season;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.List;
import java.util.Set;

@Repository
public interface StageRepository extends BasicRepository<Stage> {
@Query("select c from Criteria c where c.stage.id = ?1")
    //@Query("select  stage.criterias from Stage stage where stage.id = ?1")
<T> Set<T> getCriteriasFrom(Long id, final Class<T> type);

@EntityGraph("stage_full")
<T> T getById(Long id, final Class<T> type);

@Query("select s from StageScore s where s.stage.id = ?1")
    //@Query("select stage.stageScores from Stage stage where stage.id = ?1")
<T> Set<T> getStageScoresFrom(Long id, Class<T> type);

@Query("select s from StageSchedule s where s.stage.id = ?1")
    //@Query("select stage.stageSchedules from Stage stage where stage.id = ?1")
<T> Set<T> getStageScheduleFrom(Long id, Class<T> type);

@Query("select q from Question q where q.stage.id = ?1")
    //@Query("select stage.questions from Stage stage where stage.id = ?1")
<T> Set<T> getQuestionsFrom(Long id, Class<T> type);

<T> Page<T> getAllByOrderByTitleAscIdAsc(Pageable pageable, Class<T> type);

<T> List<T> getAllBySeason_IdOrderByTitleAsc(Long id, Class<T> type);
}