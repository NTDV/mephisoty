package ru.valkovets.mephisoty.db.service.userdata;

import com.cosium.spring.data.jpa.entity.graph.domain2.NamedEntityGraph;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.VideoUploadDto;
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
import ru.valkovets.mephisoty.api.dto.userdata.ParticipantMeDto;
import ru.valkovets.mephisoty.api.dto.userdata.StageMeDto;
import ru.valkovets.mephisoty.api.lazydata.OffsetBasedPageRequest;
import ru.valkovets.mephisoty.application.lifecycle.ticker.AchievementScoreTicker;
import ru.valkovets.mephisoty.application.services.FileSystemStorageService;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.model.season.schedule.ScheduleRecord;
import ru.valkovets.mephisoty.db.model.season.schedule.StageSchedule;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.userdata.*;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;
import ru.valkovets.mephisoty.db.projection.special.AchievementTableProj;
import ru.valkovets.mephisoty.db.projection.special.stageSchedule.StageSchedulePublicProj;
import ru.valkovets.mephisoty.db.projection.special.user.UserTableProj;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.SeasonScoreRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.qa.AnswerRepository;
import ru.valkovets.mephisoty.db.repository.season.schedule.ScheduleRecordRepository;
import ru.valkovets.mephisoty.db.repository.season.schedule.StageScheduleRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.StageScoreRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.portfolio.AchievementRepository;
import ru.valkovets.mephisoty.db.repository.userdata.GroupRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.UserRole;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.valkovets.mephisoty.application.lifecycle.Init.*;
import static ru.valkovets.mephisoty.settings.ParticipantState.PARTICIPANT;

@Service
@RequiredArgsConstructor
public class UserService {
private final FileSystemStorageService fileService;

private final UserRepository userRepository;
private final StageRepository stageRepository;
private final SpelAwareProxyProjectionFactory projectionFactory;
private final AchievementRepository achievementRepository;
private final StageScoreRepository stageScoreRepository;
private final SeasonScoreRepository seasonScoreRepository;
private final SeasonRepository seasonRepository;
private final GroupRepository groupRepository;
private final FileRepository fileRepository;
private final ScheduleRecordRepository scheduleRecordRepository;
private final AnswerRepository answerRepository;
private final StageScheduleRepository stageScheduleRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<IdTitleProj> getAllForSelect(final Specification<User> specification, final long offset, final long limit) {
  if (offset < 0 || limit < 1) return Page.empty();

  return userRepository
      .findBy(Specification.where(specification),
              q -> q.sortBy(Sort.by(Sort.Direction.ASC, User_.GROUP + "." + Group_.TITLE, User_.FULL_NAME))
                    .as(UserSelectProj.class)
                    .page(new OffsetBasedPageRequest(offset, limit)))
      .map(IdTitleFromUser::new);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public AchievementTableProj createAchievement(final Long participantId, final Long stageId, final AchievementDto dto) {
  final boolean participantExists = userRepository.existsById(participantId);
  final boolean stageExists = stageRepository.existsById(stageId);

  if (!participantExists || !stageExists) {
    throw new EntityNotFoundException(participantExists ? "Участник не найден" : "Этап не найден");
  }

  AchievementScoreTicker.addForEvaluation(participantId, stageId);

  return projectionFactory.createProjection(AchievementTableProj.class,
                                            achievementRepository.save(Achievement.from(participantId, stageId, dto)));
}

@PreAuthorize("isAuthenticated()")
@Transactional
public ParticipantMeDto getMeFor(final Long userId) {
  final User user = userRepository.findById(userId).orElseThrow();
  final Season season = seasonRepository.findById(_2024_SEASON_ID).orElseThrow();
  final Season seasonFinal = seasonRepository.findById(_2024_SEASON_FINAL_ID).orElseThrow();

  final SeasonScore seasonScore;
  if (seasonRepository.findById(_2024_SEASON_ID)
                      .map(Season::getScoreVisibility)
                      .orElse(AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name())
                      .equals(AllowState.ALLOW_READ_FOR_PARTICIPANTS.name())) {
    seasonScore = seasonScoreRepository.findBySeason_IdAndParticipant_Id(_2024_SEASON_ID, userId).orElse(null);
  } else {
    seasonScore = null;
  }

  final SeasonScore seasonFinalScore;
  if (seasonRepository.findById(_2024_SEASON_FINAL_ID)
                      .map(Season::getScoreVisibility)
                      .orElse(AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name())
                      .equals(AllowState.ALLOW_READ_FOR_PARTICIPANTS.name())) {
    seasonFinalScore = seasonScoreRepository.findBySeason_IdAndParticipant_Id(_2024_SEASON_FINAL_ID, userId).orElse(null);
  } else {
    seasonFinalScore = null;
  }


  final StageMeDto portfolioStage = stageScoreRepository
      .findByStage_IdAndParticipant_Id(_2024_PORTFOLIO_STAGE_ID, userId)
      .map(StageMeDto::from)
      .orElse(null);


  final Map<Boolean, List<Stage>> finalAndNonFinal = user
      .getChosenStages()
                                                         .stream()
      .sorted(Comparator.comparing(Stage::getId))
                                                         .collect(Collectors.partitioningBy(
                                                             stage -> stage.getId() >= _2024_PORTFOLIO_STAGE_ID));
  final Map<Long, StageScore> appliedStageScoresByStageId = stageScoreRepository
      .getAllByParticipant_Id(userId)
                                                                                .stream()
                                                                                .collect(Collectors.toMap(
                                                                                    score -> score.getStage().getId(),
                                                                                    score -> score, (s1, s2) -> s1));
  final List<StageMeDto> appliedDtos = finalAndNonFinal
      .get(false)
      .stream()
      .map(stage -> stage.getScoreVisibility().equals(AllowState.YES) ?
                    appliedStageScoresByStageId.getOrDefault(
                        stage.getId(), new StageScore(stage, user, 0f)) :
                    new StageScore(stage, user, null))
      .map(stageScore -> {
        final String additionalInfo =
            switch (stageScore.getStage().getId().intValue()) {
              case 1 -> ""; // Матбои
              case 2 -> ""; // Хакатон
              case 3 -> ""; // ЧГК
              case 4 -> scheduleRecordRepository.getStateForWirepark(userId); // Веревочный парк
              case 5 -> ""; // Бег
              case 6 -> answerRepository.getStateForVideo(userId); // Видео
              case 7 -> scheduleRecordRepository.getStateForDictant(userId); // Диктант
              case 8 -> ""; // Соц
              case 9 -> ""; // Наука
              case 10 -> ""; // Портфолио
              default -> "";
            };

        return StageMeDto.from(stageScore, additionalInfo);
      })
      .toList();

  final List<StageMeDto> appliedFinalDtos = finalAndNonFinal
      .get(true)
      .stream()
      .filter(stage -> stage.getId() != _2024_PORTFOLIO_STAGE_ID)
      .map(stage -> stage.getScoreVisibility().equals(AllowState.YES) ?
                    appliedStageScoresByStageId.getOrDefault(
                        stage.getId(), new StageScore(stage, user, 0f)) :
                    new StageScore(stage, user, null))
      .map(StageMeDto::from)
      .toList();

  final Long lastPosition = userRepository.countAllByState(PARTICIPANT.name());

  return ParticipantMeDto.from(user, lastPosition,
                               portfolioStage,
                               appliedDtos, appliedFinalDtos,
                               seasonScore, seasonFinalScore,
                               season, seasonFinal);
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).PARTICIPANT)")
@Transactional
public void applyToStage(final Long id, final Long stageId) {
  final User user = userRepository.findById(id).orElseThrow();
  final Stage stage = stageRepository.findById(stageId).orElseThrow();

  if (!user.isBanned() && stage.getApplyVisibility().equals(AllowState.YES.name())) {
    final Set<Stage> chosenStages = user.getChosenStages();

    if (!chosenStages.contains(stage)) {
      chosenStages.add(stage);
      userRepository.save(user);
    }
  } else {
    throw new AccessDeniedException("Нельзя записаться на этот этап");
  }
}

@PreAuthorize("isAuthenticated()")
@Transactional
public void updateMeFor(final Long id, final ParticipantMeDto participantMeDto) {
  User user = userRepository.findById(id).orElseThrow();
  boolean isChanged = false;

  if (user.getIsNew()) {
    if (StringUtils.isNotBlank(participantMeDto.groupTitle())) {
      final String upperTitle = participantMeDto.groupTitle().toUpperCase();
      final String fullName =
          String.join(" ", participantMeDto.firstName(), participantMeDto.secondName(), participantMeDto.thirdName()).trim();

      final Set<User> users = userRepository.findAllByFullNameAndGroup_TitleAndCredentialsIsNull(fullName, upperTitle);
      if (users.size() == 1) {
        //userRepository.delete(user);
        final User newUser = users.iterator().next();
        user.setComment("[[replaced with " + newUser.getId() + " ]]\n" + user.getComment());
        userRepository.save(user);

        user = newUser;
        isChanged = true;
      }

      if (!Objects.equals(user.tryGetGroupTitle(), upperTitle)) {
        isChanged = true;

        user.setGroup(groupRepository
                          .findByTitle(upperTitle)
                          .orElseGet(() -> groupRepository.save(
                              Group.builder().title(upperTitle).build())));
      }
    }

    if (!user.getFirstName().equals(participantMeDto.firstName()) ||
        !user.getSecondName().equals(participantMeDto.secondName()) ||
        !user.getThirdName().equals(participantMeDto.thirdName())) {
      isChanged = true;

      user.setFirstName(participantMeDto.firstName())
          .setSecondName(participantMeDto.secondName())
          .setThirdName(participantMeDto.thirdName());
    }
  }

  final Long avatarId = user.tryGetAvatarId();
  if (!Objects.equals(avatarId, participantMeDto.avatarFileId())) {

    final File avatar = fileRepository.findById(participantMeDto.avatarFileId()).orElseThrow();

    if (avatar.getCreatedBy().equals(user.getId())) {
      if (avatarId != null) {
        //try {
        //fileSystemStorageService.delete(avatarId);

        //} catch (Exception ignored) {}
      }

      isChanged = true;

      user.setAvatar(avatar);
    }
  }

  if (!Objects.equals(user.getVkNick(), participantMeDto.vk()) ||
      !Objects.equals(user.getTgNick(), participantMeDto.tg()) ||
      !Objects.equals(user.getPhoneNumber(), participantMeDto.phone())) {
    isChanged = true;

    user.setVkNick(participantMeDto.vk())
        .setTgNick(participantMeDto.tg())
        .setPhoneNumber(participantMeDto.phone());
  }

  if (isChanged) {
    user.setIsNew(false);
    userRepository.save(user);
  }
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).PARTICIPANT)")
@Transactional
public void chooseDictantDate(final Long userId, final Long dateId) {
  if (dateId == null || dateId < 1 || dateId > 2) throw new IllegalArgumentException("dateId must be 1 or 2");

  final boolean canEdit = stageRepository.findById(_2024_DICTANT_STAGE_ID)
                                         .orElseThrow()
                                         .getScheduleAccessStateEnum()
                                         .isCanEdit();

  final User user = userRepository.findById(userId).orElseThrow();
  final Set<ScheduleRecord> scheduleRecords =
      scheduleRecordRepository.findAllByParticipant_IdAndStageSchedule_IdIn(userId, List.of(1L, 2L));
  if (scheduleRecords.size() > 1) throw new IllegalStateException("Нельзя выбрать несколько дат одновременно");

  if (!canEdit && scheduleRecords.size() == 1) {
    throw new AccessDeniedException("Нельзя сменить дату");
  }

  final ScheduleRecord scheduleRecord = scheduleRecords
      .stream()
      .findAny()
      .orElseGet(() -> ScheduleRecord
          .builder()
          .participant(user)
          .build());

  if (scheduleRecord.getStageSchedule() != null && dateId.equals(scheduleRecord.getStageSchedule().getId())) return;
  scheduleRecordRepository.save(scheduleRecord.setStageSchedule(StageSchedule.builder().id(dateId).build()));
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).PARTICIPANT)")
@Transactional
public void uploadVideo(final Long userId, final VideoUploadDto videoUploadDto) {
  if (videoUploadDto.fileId() == null && StringUtils.isBlank(videoUploadDto.url())) {
    throw new IllegalArgumentException("file or url must be not null");
  }

  final boolean canEdit = stageRepository.findById(_2024_VIDEO_STAGE_ID)
                                         .orElseThrow()
                                         .getScheduleAccessStateEnum()
                                         .isCanEdit();

  final User user = userRepository.findById(userId).orElseThrow();
  final Set<Answer> answers = answerRepository.findAllByParticipant_IdAndQuestion_Id(userId, 1L);
  if (answers.size() > 1) throw new IllegalStateException("Нельзя выбрать несколько видео одновременно");

  if (!canEdit && answers.size() == 1) {
    throw new AccessDeniedException("Нельзя сменить дату");
  }

  final Answer answer = answers
      .stream()
      .findAny()
      .orElseGet(() -> Answer
          .builder()
          .question(Question.builder().id(1L).build())
          .participant(user)
          .build());

  if (StringUtils.isNotBlank(videoUploadDto.url())) answer.setShortAnswer(videoUploadDto.url());
  if (videoUploadDto.fileId() != null) {
    final File file = fileRepository.findById(videoUploadDto.fileId()).orElseThrow();
    if (file.tryGetByCurrentUser(fileService) == null) throw new AccessDeniedException("Access denied");
    answer.setFiles(new HashSet<>(List.of(file)));
  }

  answerRepository.save(answer);
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public Page<UserTableProj> getAllForStages(final long offset, final long limit,
                                           final Specification<User> specification,
                                           final Sort sort) {
  return userRepository.findAll(Specification.where(specification),
                                new OffsetBasedPageRequest(offset, limit, sort),
                                NamedEntityGraph.loading("user_table_proj"))
                       .map(user -> projectionFactory.createProjection(UserTableProj.class, user));
}

@PreAuthorize("isAuthenticated()")
@Transactional
public Map<LocalDate, List<StageSchedulePublicProj>> getWireparkDates() {
  final OffsetDateTime now = OffsetDateTime.now();
  return stageScheduleRepository.getAvailableSchedule(_2024_WIREPARK_STAGE_ID)
                                .stream()
                                .filter(stageSchedule -> ChronoUnit.HOURS.between(now, stageSchedule.getStart()) > 3)
                                .sorted(Comparator.comparing(StageSchedule::getStart))
                                .map(stageSchedule -> projectionFactory.createProjection(StageSchedulePublicProj.class,
                                                                                         stageSchedule))
                                .collect(Collectors.groupingBy(
                                    stage -> stage.getStart().atZoneSameInstant(ZoneId.of("Europe/Moscow"))
                                                  .toLocalDate())); // todo cache ZoneId.of
}

@PreAuthorize("hasAnyAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN, " +
              "T(ru.valkovets.mephisoty.settings.UserRole).PARTICIPANT)")
@Transactional
public void chooseWireparkDate(final @Positive Long userId, final Long scheduleId) {
  if (scheduleId == null) throw new IllegalArgumentException("scheduleId must not be null");

  final Credentials credentials = Credentials.getCurrent();
  final boolean isAdmin = credentials != null && credentials.getRole() == UserRole.ADMIN;

  final boolean canEdit = isAdmin ||
                          stageRepository.findById(_2024_WIREPARK_STAGE_ID)
                                         .orElseThrow()
                                         .getScheduleAccessStateEnum()
                                         .isCanEdit();

  final User user = userRepository.findById(userId).orElseThrow();
  final StageSchedule stageSchedule = stageScheduleRepository.findById(scheduleId).orElseThrow();
  if (!isAdmin && stageSchedule.getScheduleRecords().size() >= stageSchedule.getParticipantsMax()) {
    throw new IllegalStateException("На эту дату нет мест");
  }

  final Set<ScheduleRecord> allScheduleRecords =
      scheduleRecordRepository.findAllByParticipant_IdAndStageSchedule_Stage_Id(userId, _2024_WIREPARK_STAGE_ID);
  if (allScheduleRecords.size() > 1) throw new IllegalStateException("Нельзя выбрать несколько дат одновременно");

  if (!canEdit && allScheduleRecords.size() == 1) {
    throw new AccessDeniedException("Нельзя сменить дату");
  }

  final ScheduleRecord scheduleRecord = allScheduleRecords
      .stream()
      .findAny()
      .orElseGet(() -> ScheduleRecord
          .builder()
          .participant(user)
          .build());

  if (scheduleRecord.getStageSchedule() != null && scheduleId.equals(scheduleRecord.getStageSchedule().getId())) return;

  final OffsetDateTime now = OffsetDateTime.now();
  if (!isAdmin && (
      ChronoUnit.HOURS.between(now, scheduleRecord.setStageSchedule(stageSchedule).getStageSchedule().getStart()) < 3 ||
      ChronoUnit.HOURS.between(now, stageSchedule.getStart()) < 3)) {
    throw new AccessDeniedException("Нельзя выбрать дату менее чем за 3 часа до начала");
  }

  scheduleRecordRepository.save(scheduleRecord);
}

@SuppressWarnings("LombokGetterMayBeUsed")
private record IdTitleFromUser(@Getter Long id, @Getter String title) implements IdTitleProj {
  private static final IdTitleProj EMPTY = new IdTitleFromUser(0L, "");

  public IdTitleFromUser(final UserSelectProj user) {
    this(user.getId(),
         (Objects.requireNonNullElse(user.getGroup(), EMPTY).getTitle() + " " +
          user.getSecondName() + " " + user.getFirstName() + " " + user.getThirdName()).trim());
  }
}
}