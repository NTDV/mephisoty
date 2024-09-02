package ru.valkovets.mephisoty.db.model.season.schedule;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "schedule_record")
// , uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "stage_id"})) // todo Add this with migration
public class ScheduleRecord extends BasicEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_schedule_id", nullable = false)
@NotNull
private StageSchedule stageSchedule;

// todo Add this with migration
//@NotNull
//@ManyToOne(fetch = FetchType.LAZY, optional = false)
//@JoinColumn(name = "stage_id", nullable = false)
//private Stage stage;
}
