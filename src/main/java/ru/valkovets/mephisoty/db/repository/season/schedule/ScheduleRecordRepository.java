package ru.valkovets.mephisoty.db.repository.season.schedule;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.valkovets.mephisoty.application.lifecycle.Init._2024_WIREPARK_STAGE_ID;

@Repository
public interface ScheduleRecordRepository extends BasicRepository<ScheduleRecord> {

default String getStateForDictant(@NonNull final Long userId) {
  final Set<ScheduleRecord> records = findAllByParticipant_IdAndStageSchedule_IdIn(userId, List.of(1L, 2L));
  if (records.isEmpty()) {
    return null;
  } else if (records.size() > 2) {
    return "multiple";
  } else {
    return records.iterator().next().getStageSchedule().getStart().atZoneSameInstant(ZoneId.of("Europe/Moscow"))
                  .toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }
}

Set<ScheduleRecord> findAllByParticipant_IdAndStageSchedule_IdIn(@NonNull Long userId, @NonNull Collection<Long> scheduleIds);

Set<ScheduleRecord> findAllByParticipant_IdAndStageSchedule_Id(@NonNull Long userId, @NonNull Long scheduleId);

default String getStateForWirepark(@NonNull final Long userId) {
  final Set<ScheduleRecord> records = findAllByParticipant_IdAndStageSchedule_Stage_Id(userId, _2024_WIREPARK_STAGE_ID);

  if (records.isEmpty()) {
    return null;
  } else if (records.size() > 2) {
    return "multiple";
  } else {
    return records.iterator().next().getStageSchedule().getStart().atZoneSameInstant(ZoneId.of("Europe/Moscow"))
                  .toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }
}

Set<ScheduleRecord> findAllByParticipant_IdAndStageSchedule_Stage_Id(@NonNull Long userId, @NonNull Long stageId);
}