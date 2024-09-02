package ru.valkovets.mephisoty.db.repository.season.schedule;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ScheduleRecordRepository extends BasicRepository<ScheduleRecord> {

default String getStateForDictant(@NonNull final Long userId) {
  final Set<ScheduleRecord> records = findAllByParticipant_IdAndStageSchedule_IdIn(userId, List.of(1L, 2L));
  if (records.isEmpty()) {
    return null;
  } else if (records.size() > 2) {
    return "multiple";
  } else {
    return records.iterator().next().getStageSchedule().getStart().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }
}

Set<ScheduleRecord> findAllByParticipant_IdAndStageSchedule_IdIn(@NonNull Long userId, @NonNull Collection<Long> scheduleIds);
}