package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "total_score")
public class TotalScore {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private Season season;

private User participant;
private float scoreByFormula;
}
