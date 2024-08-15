package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.annotation.Nullable;
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
@Table(name = "achievement")
public class Achievement extends BasicEntity {
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "owner_id", nullable = false)
private User owner;

@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "stage_id", nullable = false)
private Stage stage;

@NotNull
@Column(name = "type_code", nullable = false)
private Integer typeCode;

@Nullable
@Column(name = "thanks_from")
private String thanksFrom;

@Transient
public AchievementType getTypeCode() {
  return AchievementType.values[typeCode];
}

@NotNull
@Column(name = "criteria_title", nullable = false, length = 255)
private String criteriaTitle;

@NotNull
@Column(name = "type_title", nullable = false, length = 500)
private String typeTitle;

@NotNull
@Column(name = "description", nullable = false, length = 1500)
private String description;

@Nullable
@Column(name = "level_title")
private String levelTitle;

@Nullable
@Column(name = "status_title")
private String statusTitle;

@Transient
public void setTypeCode(final AchievementType typeCode) {
  this.typeCode = typeCode.ordinal();
}

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "score", nullable = false)
private Float totalScore = 0f;
}
