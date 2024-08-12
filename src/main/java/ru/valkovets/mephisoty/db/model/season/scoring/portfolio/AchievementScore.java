package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
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

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "sum", nullable = false)
private Float byPlainSum = 0f;

@NotNull
@PositiveOrZero
@Column(name = "formula")
private Float byFormulaSum;

@PositiveOrZero
@Column(name = "expert")
private Float byExpert;

@PositiveOrZero
@Column(name = "total")
private Float totalByFormula;
}
