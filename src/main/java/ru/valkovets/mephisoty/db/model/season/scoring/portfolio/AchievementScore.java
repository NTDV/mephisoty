package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "achievement_score")
public class AchievementScore extends BasicEntity {

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@ManyToOne(fetch = FetchType.EAGER, optional = false)
@JoinColumn(name = "type_id", nullable = false)
private AchievementType type;

@NotNull
@Column(name = "sum", nullable = false)
@PositiveOrZero
private Float byPlainSum = 0f;

@NotNull
@Column(name = "formula")
@PositiveOrZero
private Float byFormulaSum;

@Column(name = "expert")
@PositiveOrZero
private Float byExpert;

@Column(name = "total")
@PositiveOrZero
private Float totalByFormula;
}
