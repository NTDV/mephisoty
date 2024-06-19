package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

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
@Table(name = "achievement_score")
public class AchievementScore {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private User participant;
private AchievementType type;

private float bySum;
private float byFormula;
private float byExpert;

private float totalByFormula;
}
