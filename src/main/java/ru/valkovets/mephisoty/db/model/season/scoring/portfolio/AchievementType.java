package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.valkovets.mephisoty.db.model.userdata.User;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "achievement_type")
public class AchievementType {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;

private User createdBy;
private OffsetDateTime createdAt;
private User editedBy;
private OffsetDateTime editedAt;
private String comment;

private String title;
private String description;
private String literal; // :S - bySum, :F - byFormula, :E - byExpert

private String byFormulaFormula;
private String totalFormula;
}
