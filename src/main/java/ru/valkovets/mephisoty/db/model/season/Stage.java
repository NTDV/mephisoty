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
    @NamedAttributeNode("criterias"),
    @NamedAttributeNode("files")
})
//@EntityListeners(AuditingEntityListener.class)
public class Stage extends TdrseEntity {

@JsonBackReference // todo Не будет этого поля вообще выводиться
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "season_id", nullable = false)
private Season season;

//@NotBlank
@Column(name = "literal", length = 100)
@Nullable
private @Length(max = 100)
@Pattern(regexp = ValidationConst.LITERAL_PATTERN) String literal;

@Column(name = "formula", length = 1000)
private @Length(max = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN) String stageResultFormula;
    // math + Stage criterias + AchievmentTypes (only total?)

@JsonManagedReference
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
@Builder.Default
private @NotNull Set<Criteria> criterias = new LinkedHashSet<>();

//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "stage_visibility", nullable = false)
private @NotNull String stageVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "apply_visibility", nullable = false)
private @NotNull String applyVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "score_visibility", nullable = false)
private @NotNull String scoreVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "schedule_visibility", nullable = false)
private @NotNull String scheduleAccessState = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

@Builder.Default
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "stage_files",
           joinColumns = @JoinColumn(name = "stage_id", referencedColumnName = "id"),
           inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
private @NotNull Set<File> files = new LinkedHashSet<>();

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

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private @NotNull Set<StageScore> stageScoresForParticipants = new LinkedHashSet<>();

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private @NotNull Set<StageSchedule> stageSchedules = new LinkedHashSet<>();

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private @NotNull Set<Question> questions = new LinkedHashSet<>();

@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private @NotNull List<Achievement> achievements = new ArrayList<>();

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
