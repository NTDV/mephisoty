package ru.valkovets.mephisoty.db.model.season.scoring.portfolio;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
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
@Column(name = "thanks_from", length = 255)
private String thanksFrom;
@Nullable
@Column(name = "level_title", length = 255)
private String levelTitle;

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
@Column(name = "status_title", length = 255)
private String statusTitle;

public static Achievement from(final Long participantId, final Long stageId, final AchievementDto dto) {
  return Achievement.builder()
                    .owner(User.builder().id(participantId).build())
                    .stage(Stage.builder().id(stageId).build())
                    .build()
                    .editFrom(dto);
}

@Transient
public void setTypeCode(final AchievementType typeCode) {
  this.typeCode = typeCode.ordinal();
}

@NotNull
@PositiveOrZero
@Builder.Default
@Column(name = "score", nullable = false)
private Float totalScore = 0f;

public Achievement editFrom(final AchievementDto dto) {
  setComment(dto.comment());
  typeCode = dto.typeCode();
  criteriaTitle = dto.criteriaTitle();
  typeTitle = dto.typeTitle();
  description = dto.description();
  levelTitle = dto.levelTitle();
  statusTitle = dto.statusTitle();
  totalScore = dto.totalScore();
  thanksFrom = dto.thanksFrom();
  return this;
}

@Transient
public AchievementType getTypeCode() {
  return AchievementType._VALUES[typeCode];
}
}
