package ru.valkovets.mephisoty.db.model.season.timing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule_record")
public class ScheduleRecord {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private User participant;
private StageSchedule stageSchedule;
}
