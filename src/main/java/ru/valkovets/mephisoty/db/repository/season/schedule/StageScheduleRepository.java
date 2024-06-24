package ru.valkovets.mephisoty.db.repository.season.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;

@Repository
public interface StageScheduleRepository extends JpaRepository<StageSchedule, Long> {
    
}