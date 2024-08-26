package ru.valkovets.mephisoty.db.service.userdata;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
import ru.valkovets.mephisoty.api.dto.userdata.ParticipantMeDto;
import ru.valkovets.mephisoty.api.dto.userdata.StageMeDto;
import ru.valkovets.mephisoty.api.lazydata.OffsetBasedPageRequest;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.application.lifecycle.ticker.AchievementScoreTicker;
import ru.valkovets.mephisoty.application.services.FileSystemStorageService;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.model.userdata.Group_;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;
import ru.valkovets.mephisoty.db.projection.special.AchievementTableProj;
import ru.valkovets.mephisoty.db.repository.UtilsRepository;
import ru.valkovets.mephisoty.db.repository.files.FileRepository;
import ru.valkovets.mephisoty.db.repository.season.SeasonRepository;
import ru.valkovets.mephisoty.db.repository.season.SeasonScoreRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.StageScoreRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.portfolio.AchievementRepository;
import ru.valkovets.mephisoty.db.repository.userdata.GroupRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.AllowState;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepository userRepository;
private final StageRepository stageRepository;
private final SpelAwareProxyProjectionFactory projectionFactory;
private final AchievementRepository achievementRepository;
private final StageScoreRepository stageScoreRepository;
private final SeasonScoreRepository seasonScoreRepository;
private final SeasonRepository seasonRepository;
private final GroupRepository groupRepository;
private final FileRepository fileRepository;
private final UtilsRepository utilsRepository;
private final FileSystemStorageService fileSystemStorageService;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<IdTitleProj> getAllForSelect(final Specification<User> specification, final long page, final long size) {
  return userRepository
      .findBy(Specification.where(specification),
              q -> q.sortBy(Sort.by(Sort.Direction.ASC, User_.GROUP + "." + Group_.TITLE, User_.FULL_NAME))
                    .as(UserSelectProj.class)
                    .page(new OffsetBasedPageRequest(page, size)))
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
  final Season season = seasonRepository.findById(Init._2024_SEASON_ID).orElseThrow();
  final Season seasonFinal = seasonRepository.findById(Init._2024_SEASON_FINAL_ID).orElseThrow();

  final SeasonScore seasonScore;
  if (seasonRepository.findById(Init._2024_SEASON_ID)
                      .map(Season::getScoreVisibility)
                      .orElse(AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name())
                      .equals(AllowState.ALLOW_READ_FOR_PARTICIPANTS.name())) {
    seasonScore = seasonScoreRepository.findBySeason_IdAndParticipant_Id(Init._2024_SEASON_ID, userId).orElse(null);
  } else {
    seasonScore = null;
  }

  final SeasonScore seasonFinalScore;
  if (seasonRepository.findById(Init._2024_SEASON_FINAL_ID)
                      .map(Season::getScoreVisibility)
                      .orElse(AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name())
                      .equals(AllowState.ALLOW_READ_FOR_PARTICIPANTS.name())) {
    seasonFinalScore = seasonScoreRepository.findBySeason_IdAndParticipant_Id(Init._2024_SEASON_FINAL_ID, userId).orElse(null);
  } else {
    seasonFinalScore = null;
  }


  final StageMeDto portfolioStage = stageScoreRepository.findByStage_IdAndParticipant_Id(Init._2024_PORTFOLIO_STAGE_ID, userId)
                                                        .map(StageMeDto::from).orElse(null);


  final Map<Boolean, List<Stage>> finalAndNonFinal = user.getChosenStages()
                                                         .stream()
                                                         .collect(Collectors.partitioningBy(
                                                             stage -> stage.getId() >= Init._2024_PORTFOLIO_STAGE_ID));
  final Map<Long, StageScore> appliedStageScoresByStageId = stageScoreRepository.getAllByParticipant_Id(userId)
                                                                                .stream()
                                                                                .collect(Collectors.toMap(
                                                                                    score -> score.getStage().getId(),
                                                                                    score -> score, (s1, s2) -> s1));
  final List<StageMeDto> appliedDtos = finalAndNonFinal.get(false)
                                                       .stream()
                                                       .map(stage -> stage.getScoreVisibility().equals(AllowState.YES) ?
                                                                     appliedStageScoresByStageId.getOrDefault(stage.getId(),
                                                                                                              new StageScore(
                                                                                                                  stage, user,
                                                                                                                  0f)) :
                                                                     new StageScore(stage, user, null))
                                                       .map(StageMeDto::from)
                                                       .toList();

  final List<StageMeDto> appliedFinalDtos = finalAndNonFinal.get(true)
                                                            .stream()
                                                            .filter(stage -> stage.getId() != Init._2024_PORTFOLIO_STAGE_ID)
                                                            .map(stage -> stage.getScoreVisibility().equals(AllowState.YES) ?
                                                                          appliedStageScoresByStageId.getOrDefault(
                                                                              stage.getId(), new StageScore(stage, user, 0f)) :
                                                                          new StageScore(stage, user, null))
                                                            .map(StageMeDto::from)
                                                            .toList();

  return ParticipantMeDto.from(user, portfolioStage,
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
        userRepository.delete(user);
        user = users.iterator().next();
        isChanged = true;
      }

      if (!Objects.equals(user.tryGetGroupTitle(), upperTitle)) {
        isChanged = true;

        user.setGroup(groupRepository
                          .findByTitle(upperTitle)
                          .orElse(groupRepository.save(
                              Group.builder()
                                   .title(upperTitle)
                                   .build())));
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
