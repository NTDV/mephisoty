package ru.valkovets.mephisoty.db.model.season;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.settings.VisibilityState;

import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stage")
public class Stage {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private Season season;

private String title;
private String description;
private String rules;
private OffsetDateTime start;
private OffsetDateTime end;
private String literal;

private String stageResultFormula; // math + Stage criterias + AchievmentTypes (only total?)

private VisibilityState stageVisibility;
private VisibilityState scoreVisibility;
}
