package ru.valkovets.mephisoty.db.model.season.scoring;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.Stage;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "criteria")
public class Criteria {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private Stage stage;

private String title;
private String description;
private String literal;

private float max;
private float min;
}
