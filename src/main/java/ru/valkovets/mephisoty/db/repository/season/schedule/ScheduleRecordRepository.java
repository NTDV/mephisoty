package ru.valkovets.mephisoty.db.repository.season.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;

@Repository
public interface ScheduleRecordRepository extends JpaRepository<ScheduleRecord, Long> {
    
}