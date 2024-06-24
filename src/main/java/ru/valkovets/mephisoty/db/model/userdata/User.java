package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.AchievementScore;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.settings.ParticipantState;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "app_user")
public class User extends BasicEntity {
// todo Везде проставить аннотации + валидации

@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
@JoinColumn(name = "avatar_id")
private File avatar;

@NotNull
@Enumerated
@Column(name = "state", nullable = false)
private ParticipantState state;

@Length(max = 50)
@NotBlank
@Column(name = "first_name", nullable = false, length = 50)
private String firstName;

@Length(max = 50)
@NotBlank
@Column(name = "second_name", nullable = false, length = 50)
private String secondName;

@Length(max = 50)
@Column(name = "third_name", length = 30)
@NotNull
private String thirdName = "";

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "group_id")
@Nullable
private Group group;

@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
@Nullable
private Credentials credentials;

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
private Set<File> files = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "expert", orphanRemoval = true)
private Set<CriteriaScore> criteriaScoresAsExpert = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<CriteriaScore> criteriaScores = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<StageScore> stageScores = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<SeasonScore> seasonScores = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
private Set<Achievement> achievements = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<AchievementScore> achievementScores = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<ScheduleRecord> scheduleRecords = new LinkedHashSet<>();

@NotNull
@ManyToMany(fetch = FetchType.LAZY, mappedBy = "experts")
private Set<StageSchedule> stageSchedulesAsExpert = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<Answer> answers = new LinkedHashSet<>();

@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<Answer> answersAsExpert = new LinkedHashSet<>();

}
