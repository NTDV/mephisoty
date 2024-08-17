package ru.valkovets.mephisoty.db.service.userdata;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
import ru.valkovets.mephisoty.api.lazydata.OffsetBasedPageRequest;
import ru.valkovets.mephisoty.application.lifecycle.ticker.AchievementScoreTicker;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.userdata.Group_;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.model.userdata.User_;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSelectProj;
import ru.valkovets.mephisoty.db.projection.special.AchievementTableProj;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.portfolio.AchievementRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
private final UserRepository userRepository;
private final StageRepository stageRepository;
private final SpelAwareProxyProjectionFactory projectionFactory;
private final AchievementRepository achievementRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<IdTitleProj> getAllForSelect(final Specification<User> specification, final long page, final long size) {
  return userRepository
      .findBy(Specification.where(specification),
              q -> q.sortBy(Sort.by(Sort.Direction.ASC, User_.GROUP + "." + Group_.TITLE, User_.SECOND_NAME, User_.FIRST_NAME,
                                    User_.THIRD_NAME))
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
