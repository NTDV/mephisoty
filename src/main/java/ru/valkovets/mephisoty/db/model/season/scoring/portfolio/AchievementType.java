package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "achievement_type")
public class AchievementType extends TdrEntity {

@Length(max = 100)
@Column(name = "literal", nullable = false, unique = true, length = 100)
@Pattern(regexp = ValidationConst.LITERAL_PATTERN)
@NotBlank
private String literal; // :S - bySum, :F - byFormula, :E - byExpert

@Length(max = 1000)
@Column(name = "sum_formula", length = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN)
@NotBlank
private String typeSumFormula;

@Length(max = 1000)
@Column(name = "total_formula", length = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN)
@NotBlank
private String typeTotalFormula;

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "achievementType", orphanRemoval = true)
private Set<Achievement> achievements = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "type", orphanRemoval = true)
private Set<AchievementScore> achievementScores = new LinkedHashSet<>();

}
