package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.timing.ScheduleRecord;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "criteria_score")
public class CriteriaScore {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private Criteria criteria;

private User expert;
private User participant;
private float score;
}
