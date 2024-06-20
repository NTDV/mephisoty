package ru.valkovets.mephisoty.db.model.season;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AllowState;

import java.time.OffsetDateTime;

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

private User createdBy;
private OffsetDateTime createdAt;
private User editedBy;
private OffsetDateTime editedAt;
private String comment;

private Season season;

private String title;
private String description;
private String rules;
private OffsetDateTime start;
private OffsetDateTime end;
private String literal;

private String stageResultFormula; // math + Stage criterias + AchievmentTypes (only total?)

private AllowState stageVisibility;
private AllowState scoreVisibility;
private AllowState scheduleAccessState;
}
