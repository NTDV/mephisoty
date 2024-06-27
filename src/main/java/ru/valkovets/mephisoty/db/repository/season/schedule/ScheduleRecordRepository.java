package ru.valkovets.mephisoty.db.repository.season.schedule;

import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface ScheduleRecordRepository extends BasicRepository<ScheduleRecord> {
    
}