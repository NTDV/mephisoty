package ru.valkovets.mephisoty.db.model.season.schedule;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
public class StageSchedule extends TdrseEntity {

@NotNull
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private Stage stage;

@PositiveOrZero
@Column(name = "participants_max")
private Integer participantsMax;

@Builder.Default
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "user_stage_schedules_as_expert",
           joinColumns = @JoinColumn(name = "stage_schedule_id"),
           inverseJoinColumns = @JoinColumn(name = "expert_id"))
private Set<User> experts = new LinkedHashSet<>();

@NotNull
@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "state")
private AllowState state = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stageSchedule", orphanRemoval = true)
private Set<ScheduleRecord> scheduleRecords = new LinkedHashSet<>();

}
