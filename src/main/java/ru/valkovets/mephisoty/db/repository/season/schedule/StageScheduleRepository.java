package ru.valkovets.mephisoty.db.repository.season.schedule;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.util.List;

@Repository
public interface StageScheduleRepository extends BasicRepository<StageSchedule> {
@EntityGraph("stage_schedule_full")
<T> T getById(Long id, final Class<T> type);

List<StageSchedule> findAllByStage_Id(Long stageId);

@Query("select ss from StageSchedule ss where ss.stage.id = :stageId and size(ss.scheduleRecords) < ss.participantsMax")
List<StageSchedule> getAvailableSchedule(Long stageId);
}