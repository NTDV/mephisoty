package ru.valkovets.mephisoty.db.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.valkovets.mephisoty.api.dto.userdata.UserRegistrationDto;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.CriteriaScore;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.AchievementScore;
import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.security.credentials.CasUserXml;
import ru.valkovets.mephisoty.settings.ParticipantState;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(
    name = "app_user"
    //,indexes = {
    //    @Index(name = "app_user_fullname_index", columnList = User_.FULL_NAME, unique = false),
    //}
    )
@EntityListeners(AuditingEntityListener.class)
public class User extends BasicEntity {
// todo Везде проставить аннотации + валидации

@JsonIgnore
@Nullable
@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
@JoinColumn(name = "credentials_id", unique = true)
private Credentials credentials;

@ManyToOne(fetch = FetchType.LAZY,
           cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
@JoinColumn(name = "avatar_id")
private File avatar;

@NotNull
//@Enumerated(EnumType.STRING)
@Builder.Default
@Column(name = "state", nullable = false)
private String state = ParticipantState.NOT_PARTICIPANT.name();
@NotNull
@Builder.Default
@Length(max = 50)
@Column(name = "third_name", length = 100)
private String thirdName = "";
@NotNull
@Length(max = 50)
@Builder.Default
@Column(name = "first_name", nullable = false, length = 100)
private String firstName = "";
@NotNull
@Length(max = 50)
@Builder.Default
@Column(name = "second_name", nullable = false, length = 100)
private String secondName = "";
@NotNull
@Builder.Default
@Column(name = "is_new", nullable = false)
private Boolean isNew = true;

@Formula("concat_ws(' ', second_name, first_name, nullif(third_name, ''))")
private String fullName;
@NotNull
@Builder.Default
@Column(name = "vk_nick", nullable = true, length = 255)
private String vkNick = "";
@NotNull
@Builder.Default
@Column(name = "tg_nick", nullable = true, length = 255)
private String tgNick = "";
@NotNull
@Builder.Default
@Column(name = "phone_number", nullable = true)
private String phoneNumber = "";
@NotNull
@Builder.Default
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(name = "user_stages",
           joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
           inverseJoinColumns = @JoinColumn(name = "stage_id", referencedColumnName = "id"))
private Set<Stage> chosenStages = new LinkedHashSet<>();

public static User newEmpty() {
  return User.builder()
             .firstName("")
             .secondName("")
             .thirdName("")
             .state(ParticipantState.NOT_PARTICIPANT.name())
             .build();
}

@Transient
public ParticipantState getState() {
  return ParticipantState.valueOf(state);
}

@Transient
public Long tryGetAvatarId() {
  return avatar == null ? null : avatar.getId();
}

public void setState(final ParticipantState state) {
  this.state = state.name();
}

@Nullable
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "group_id")
private Group group;

public static User from(final UserRegistrationDto dto) {
  return User.builder()
             .comment(dto.comment())
             .state(dto.state().name())
             .firstName(dto.firstName())
             .secondName(dto.secondName())
             .thirdName(dto.thirdName())
             .build();
}

public static User from(final CasUserXml.AuthenticationSuccess.Attributes userDataAttributes) {
  final String[] name = new String[] { "", "", "" };
  final String[] nameSplit = userDataAttributes.getFullName().split(" ", 3);
  if (nameSplit.length > 0) name[0] = nameSplit[0];
  if (nameSplit.length > 1) name[1] = nameSplit[1];
  if (nameSplit.length > 2) name[2] = nameSplit[2];

  return User.builder()
             .firstName(name[1])
             .secondName(name[0])
             .thirdName(name[2])
             .state(ParticipantState.PARTICIPANT.name())
             .build();
}

@Transient
public boolean isBanned() {
  final ParticipantState participantState = getState();
  return participantState == ParticipantState.BANNED ||
         participantState == ParticipantState.DISQUALIFIED;
}

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "expert", orphanRemoval = true)
private Set<CriteriaScore> criteriaScoresAsExpert = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<CriteriaScore> criteriaScores = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<StageScore> stageScores = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<SeasonScore> seasonScores = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", orphanRemoval = true)
private Set<Achievement> achievements = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<AchievementScore> achievementScores = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<ScheduleRecord> scheduleRecords = new LinkedHashSet<>();

@NotNull
@Builder.Default
@ManyToMany(fetch = FetchType.LAZY, mappedBy = "experts")
private Set<StageSchedule> stageSchedulesAsExpert = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<Answer> answers = new LinkedHashSet<>();

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "participant", orphanRemoval = true)
private Set<Answer> answersAsExpert = new LinkedHashSet<>();

@Transient
public String tryGetGroupTitle() {
  return group == null ? null : group.getTitle();
}
}
