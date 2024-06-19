package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

private String title;
private String description;
private String literal; // :S - bySum, :F - byFormula, :E - byExpert

private String byFormulaFormula;
private String totalFormula;
}
