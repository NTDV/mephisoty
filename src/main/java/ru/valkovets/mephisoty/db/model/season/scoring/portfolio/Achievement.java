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
@Table(name = "achievement")
public class Achievement {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private User owner;
private String title;
private String description;
private float pgasScore;

private AchievementType achievementType;
}
