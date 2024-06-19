package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stage_score")
public class StageScore {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private Stage stage;

private User participant;
private float scoreByFormula;
}
