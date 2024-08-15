package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AchievementType;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "achievement_score",
       uniqueConstraints = @UniqueConstraint(columnNames = { "participant_id", "stage_id", "type_code" }))
public class AchievementScore extends BasicEntity {
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "participant_id", nullable = false)
@NotNull
private User participant;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private Stage stage;

@NotNull
@Column(name = "type_code", nullable = false)
private Integer typeCode;

@Transient
public AchievementType getTypeCode() {
  return AchievementType.values[typeCode];
}

@Transient
public void setTypeCode(final AchievementType typeCode) {
  this.typeCode = typeCode.ordinal();
}

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
}
