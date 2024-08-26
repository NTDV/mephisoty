package ru.valkovets.mephisoty.db.model.season;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "stage")
@NamedEntityGraph(name = "stage_full", attributeNodes = {
    @NamedAttributeNode("createdAt"),
    @NamedAttributeNode("modifiedAt"),
    @NamedAttributeNode("createdBy"),
    @NamedAttributeNode("modifiedBy"),
    @NamedAttributeNode("season"),
    @NamedAttributeNode("criterias")
})
//@EntityListeners(AuditingEntityListener.class)
public class Stage extends TdrseEntity {

@JsonBackReference // todo Не будет этого поля вообще выводиться
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "season_id", nullable = false)
private Season season;

@Length(max = 100)
@Column(name = "literal", length = 100)
@Nullable
@Pattern(regexp = ValidationConst.LITERAL_PATTERN)
//@NotBlank
private String literal;

@Length(max = 1000)
@Column(name = "formula", length = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN)
private String stageResultFormula; // math + Stage criterias + AchievmentTypes (only total?)

@JsonManagedReference
@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
@Builder.Default
private Set<Criteria> criterias = new LinkedHashSet<>();

@NotNull
//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "stage_visibility", nullable = false)
private String stageVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

@NotNull
//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "apply_visibility", nullable = false)
private String applyVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

@NotNull
//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "score_visibility", nullable = false)
private String scoreVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

@NotNull
//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "schedule_visibility", nullable = false)
private String scheduleAccessState = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

@NotNull
@Builder.Default

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "stage_files",
           joinColumns = @JoinColumn(name = "stage_id", referencedColumnName = "id"),
           inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
private Set<File> files = new LinkedHashSet<>();

public static Stage createFrom(final StageDto stageDto, final Season season) {
  return Stage.builder()
              .season(season)
              .comment(stageDto.comment())
              .title(stageDto.title())
              .description(stageDto.description())
              .rules(stageDto.rules())
              .start(stageDto.start())
              .end(stageDto.end())
              .literal(stageDto.literal())
              .stageResultFormula(stageDto.stageResultFormula())
              .stageVisibility(stageDto.stageVisibility().name())
              .applyVisibility(stageDto.applyVisibility().name())
              .scoreVisibility(stageDto.scoreVisibility().name())
              .scheduleAccessState(stageDto.scheduleAccessState().name())
              .build();
}

@Transient
public Long tryGetFileId(final String code) {
  return files.stream().filter(file -> Objects.equals(file.getCode(), code)).map(File::getId).findFirst().orElse(null);
}

@Transient
public Stage addFile(final File file) {
  if (file != null) files.add(file);
  return this;
}

@Transient
public AllowState getStageVisibilityEnum() {
  return AllowState.valueOf(stageVisibility);
}

@Transient
public AllowState getScoreVisibilityEnum() {
  return AllowState.valueOf(scoreVisibility);
}

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private Set<StageScore> stageScoresForParticipants = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private Set<StageSchedule> stageSchedules = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private Set<Question> questions = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private List<Achievement> achievements = new ArrayList<>();

@Transient
public AllowState getScheduleAccessStateEnum() {
  return AllowState.valueOf(scheduleAccessState);
}

public Stage editFrom(final StageDto dto) {
  setComment(dto.comment());
  setTitle(dto.title());
  setDescription(dto.description());
  setRules(dto.rules());
  setStart(dto.start());
  setEnd(dto.end());
  literal = dto.literal();
  stageResultFormula = dto.stageResultFormula();
  stageVisibility = dto.stageVisibility().name();
  applyVisibility = dto.applyVisibility().name();
  scoreVisibility = dto.scoreVisibility().name();
  scheduleAccessState = dto.scheduleAccessState().name();
  return this;
}
}
