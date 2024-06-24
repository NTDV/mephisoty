package ru.valkovets.mephisoty.db.model.season.schedule;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AllowState;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "stage_schedule")
public class StageSchedule extends TdrseEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
@NotNull
private Stage stage;

@Column(name = "participants_max")
@PositiveOrZero
private Integer participantsMax;

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "user_stage_schedules_as_expert",
           joinColumns = @JoinColumn(name = "stage_schedule_id"),
           inverseJoinColumns = @JoinColumn(name = "expert_id"))
private Set<User> experts = new LinkedHashSet<>();

@NotNull
@Enumerated
@Column(name = "state")
private AllowState state = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@OneToMany(fetch = FetchType.LAZY, mappedBy = "stageSchedule", orphanRemoval = true)
private Set<ScheduleRecord> scheduleRecords = new LinkedHashSet<>();

}
