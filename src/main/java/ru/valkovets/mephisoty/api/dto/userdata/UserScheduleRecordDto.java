package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.time.OffsetDateTime;

public record UserScheduleRecordDto(
    @Nullable Long id,
    @Nullable String fullName,
    @Nullable String group,
    @Nullable String vkNick,
    @Nullable String tgNick,
    @Nullable String phoneNumber,

    @Nullable Long recordId,
    @Nullable OffsetDateTime dateTime) {
public static UserScheduleRecordDto from(final User user, final ScheduleRecord scheduleRecord) {
  final Group group = user.getGroup();
  final StageSchedule stageSchedule = scheduleRecord == null ? null : scheduleRecord.getStageSchedule();

  return new UserScheduleRecordDto(
      user.getId(),
      user.getFullName(),
      group == null ? null : group.getTitle(),

      user.getVkNick(),
      user.getTgNick(),
      user.getPhoneNumber(),

      scheduleRecord == null ? null : scheduleRecord.getId(),
      stageSchedule == null ? null : stageSchedule.getStart());
}
}
