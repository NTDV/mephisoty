package ru.valkovets.mephisoty.db.model.season;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.api.dto.season.StageDto;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.Criteria;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "stage")
public class Stage extends TdrseEntity {

@JsonBackReference // todo Не будет этого поля вообще выводиться
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "season_id", nullable = false)
private Season season;

@Length(max = 100)
@Column(name = "literal", nullable = false, unique = true, length = 100)
@Pattern(regexp = ValidationConst.LITERAL_PATTERN)
//@NotBlank
private String literal;

@Length(max = 1000)
@Column(name = "formula", length = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN)
private String stageResultFormula; // math + Stage criterias + AchievmentTypes (only total?)

@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
@Builder.Default
private Set<Criteria> criterias = new LinkedHashSet<>();

@NotNull
@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "stage_visibility", nullable = false)
private AllowState stageVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@NotNull
@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "score_visibility", nullable = false)
private AllowState scoreVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

@NotNull
@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "schedule_visibility", nullable = false)
private AllowState scheduleAccessState = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS;

//@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private Set<StageScore> stageScoresForParticipants = new LinkedHashSet<>();

//@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private Set<StageSchedule> stageSchedules = new LinkedHashSet<>();

//@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "stage", orphanRemoval = true)
private Set<Question> questions = new LinkedHashSet<>();

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
                .stageVisibility(stageDto.stageVisibility())
                .scoreVisibility(stageDto.scoreVisibility())
                .scheduleAccessState(stageDto.scheduleAccessState())
                .build();
}
}
