package ru.valkovets.mephisoty.db.service.stages;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.UserScheduleRecordDto;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.repository.season.schedule.ScheduleRecordRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WireparkStageService {
private static final Logger log = LoggerFactory.getLogger(WireparkStageService.class);
private final UserRepository userRepository;
private final ScheduleRecordRepository scheduleRecordRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public Page<UserScheduleRecordDto> getAll(final int page, final int size,
                                          final Specification<User> specification,
                                          final Sort sort) {
  final Page<User> participants = userRepository.findAll(
      Specification.<User>where((root, query, builder) ->
                                    builder.equal(root.join(User_.chosenStages)
                                                      .get(Stage_.id),
                                                  Init._2024_WIREPARK_STAGE_ID))
                   .and(specification),
      PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort),
      NamedEntityGraph.loading("user_table_proj"));

  final Map<Long, ScheduleRecord> recordByParticipantId = participants
      .stream()
      .flatMap(user -> scheduleRecordRepository.findAllByParticipant_IdAndStageSchedule_Stage_Id(user.getId(),
                                                                                                 Init._2024_WIREPARK_STAGE_ID)
                                               .stream())
      .collect(Collectors.toMap(
          record -> record.getParticipant().getId(),
          record -> record,
          (a1, a2) -> {
            log.error("Duplicate records found: {} and {}", a1, a2);
            return null;
          }));

  return participants.map(user -> UserScheduleRecordDto.from(user, recordByParticipantId.get(user.getId())));
}
}