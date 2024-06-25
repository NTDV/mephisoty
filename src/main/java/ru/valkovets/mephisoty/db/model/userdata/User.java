package ru.valkovets.mephisoty.db.model.userdata;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.valkovets.mephisoty.db.dto.userdata.UserDto;
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
@EntityListeners(AuditingEntityListener.class)
public class User extends BasicEntity {
// todo Везде проставить аннотации + валидации

@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
@JoinColumn(name = "avatar_id")
private File avatar;

@NotNull
@Enumerated
@Column(name = "state", nullable = false)
private ParticipantState state;

@NotBlank
@Length(max = 50)
@Column(name = "first_name", nullable = false, length = 50)
private String firstName;

@NotBlank
@Length(max = 50)
@Column(name = "second_name", nullable = false, length = 50)
private String secondName;

@NotNull
@Length(max = 30)
@Column(name = "third_name", length = 30)
private String thirdName = "";

@Nullable
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "group_id")
private Group group;

@Nullable
@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
@JoinColumn(name = "credentials_id", unique = true)
private Credentials credentials;

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
private Set<File> files = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "expert", orphanRemoval = true)
private Set<CriteriaScore> criteriaScoresAsExpert = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<CriteriaScore> criteriaScores = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<StageScore> stageScores = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<SeasonScore> seasonScores = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
private Set<Achievement> achievements = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<AchievementScore> achievementScores = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<ScheduleRecord> scheduleRecords = new LinkedHashSet<>();

//@NotNull
@ManyToMany(fetch = FetchType.LAZY, mappedBy = "experts")
private Set<StageSchedule> stageSchedulesAsExpert = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<Answer> answers = new LinkedHashSet<>();

//@NotNull
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<Answer> answersAsExpert = new LinkedHashSet<>();

public static User from(final UserDto dto) {
    return User.builder()
               .comment(dto.comment())
               .state(dto.state())
               .firstName(dto.firstName())
               .secondName(dto.secondName())
               .thirdName(dto.thirdName())
               .build();
}
}
