package ru.valkovets.mephisoty.db.model.season.schedule;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.api.dto.season.StageScheduleDto;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AllowState;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "stage_schedule")
@NamedEntityGraph(name = "stage_schedule_full", attributeNodes = {
    @NamedAttributeNode("createdAt"),
    @NamedAttributeNode("modifiedAt"),
    @NamedAttributeNode("createdBy"),
    @NamedAttributeNode("modifiedBy"),
    @NamedAttributeNode("stage")
})
public class StageSchedule extends TdrseEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private @NotNull Stage stage;

@Column(name = "participants_max")
private @PositiveOrZero Integer participantsMax;

@Builder.Default
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "user_stage_schedules_as_expert",
           joinColumns = @JoinColumn(name = "stage_schedule_id"),
           inverseJoinColumns = @JoinColumn(name = "expert_id"))
private Set<User> experts = new LinkedHashSet<>();

@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "state")
private @NotNull AllowState state = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stageSchedule", orphanRemoval = true)
private Set<ScheduleRecord> scheduleRecords = new LinkedHashSet<>();

public static StageSchedule from(final StageScheduleDto scheduleDto, final Stage stage) {
  return StageSchedule.builder()
                      .stage(stage)
                      .comment(scheduleDto.comment())
                      .description(scheduleDto.description())
                      .start(scheduleDto.start())
                      .end(scheduleDto.end())
                      .state(scheduleDto.state())
                      .participantsMax(scheduleDto.participantsMax())
                      .build();
}

public StageSchedule editFrom(final StageScheduleDto dto) {
  setComment(dto.comment());
  setDescription(dto.description());
  setStart(dto.start());
  setEnd(dto.end());
  setState(dto.state());
  setParticipantsMax(dto.participantsMax());

  return this;
}
}
