package ru.valkovets.mephisoty.db.service.season.scoring.portfolio;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.CsvUploadDto;
import ru.valkovets.mephisoty.api.dto.season.AchievementDto;
import ru.valkovets.mephisoty.application.lifecycle.ticker.AchievementScoreTicker;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement_;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.special.AchievementTableProj;
import ru.valkovets.mephisoty.db.repository.UtilsRepository;
import ru.valkovets.mephisoty.db.repository.season.StageRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.portfolio.AchievementRepository;
import ru.valkovets.mephisoty.db.repository.userdata.GroupRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.settings.ParticipantState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {
private final ProjectionFactory projectionFactory;

private final GroupRepository groupRepository;
private final UserRepository userRepository;
private final AchievementRepository achievementRepository;
private final StageRepository stageRepository;
private final UtilsRepository utilsRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public Page<AchievementTableProj> getAllFor(final int page, final int size, final Specification<Achievement> specification,
                                            final Sort sort) {
  return achievementRepository.findBy(Specification.where(specification),
                                      q -> q.sortBy(sort == null ? Sort.by(Achievement_.TYPE_CODE) : sort)
                                            .as(AchievementTableProj.class)
                                            .page(PageRequest.of(page, size)));
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public List<String> importNew(@NotNull final CsvUploadDto file, final Long stageId) throws IOException {
  final Stage stage = stageRepository.findById(stageId).orElseThrow();
  final HashSet<String> groups = new HashSet<>(64);
  final HashMap<UserPrototype, Set<AchievementPrototype>> achievementsByUser = new HashMap<>(1024);

  (file.hasHeader() != null && file.hasHeader() ?
   CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true) :
   CSVFormat.DEFAULT.builder()
  ).setDelimiter(file.delimiter() == null ? ";" : file.delimiter())
   .setAllowMissingColumnNames(true)
   .build()
   .parse(new BufferedReader(new InputStreamReader(file.file().getInputStream(), StandardCharsets.UTF_8)))
   .stream()
   .forEach(row -> {
     // Иванов Иван Иванович
     final String fullName = row.get(1).trim();
     // Б22-534
     final String groupTitle = row.get(2).trim();
     // [пусто] не выполнены критерии успеваемости (есть "3"/пересдачи вне сессии)
     final String comment = row.get(17).trim();
     // 1-Учеба
     final String typeCode = row.get(19).trim();
     // Участие/победа в олимпиадах, конкурсах, соревнованиях,  турнирах, конкурсах профессионального и  инженерного мастерства
     final String criteriaTitle = row.get(20).trim();
     // Участие/победа в олимпиадах, интеллектуальных состязаниях, турнирах, конкурсах профессионального и инженерного мастерства
     final String typeTitle = row.get(21).trim();
     // Олимпиада "Я-профессионал" Экономика - Участник. Без указания места/степени. Международный уровень. Даты(а): 2023-12-27 - 2023-12-27. Дата: 2023, Ноябрь.
     final String description = row.get(22).trim();
     // [пусто] Всероссийский
     final String levelTitle = row.get(26).trim();
     // [пусто] Участник
     final String statusTitle = row.get(27).trim();
     // [пусто] От лица, курирующего соответствующее направление д
     final String thanksFrom = row.get(29).trim();
     // 12,1
     final String total = row.get(31).trim();

     groups.add(groupTitle);
     achievementsByUser.computeIfAbsent(new UserPrototype(fullName, groupTitle), k -> new HashSet<>(8))
                       .add(new AchievementPrototype(comment, typeCode, criteriaTitle, typeTitle,
                                                     description, levelTitle, statusTitle, thanksFrom, total));
   });

  final HashMap<String, Group> existingGroupByTitle = utilsRepository
      .findAllByNaturalIdIn(groups, Group.class)
      //groupRepository.findByTitleIn(groups, Group.class)
      .parallelStream()
      .collect(Collectors.toMap(Group::getTitle, group -> group, (g1, g2) -> g1, HashMap::new));
  groupRepository.saveAll(
                     groups.parallelStream()
                           .filter(title -> !existingGroupByTitle.containsKey(title))
                           .sequential()
                           .map(title -> Group.builder().title(title).build()).toList())
                 .forEach(group -> existingGroupByTitle.put(group.getTitle(), group));

  final HashMap<UserPrototype, User> existingUsersByPrototype = userRepository
      .findAllByFullNameAndGroup(achievementsByUser.keySet()
                                                   .parallelStream()
                                                   .map(UserPrototype::fullName)
                                                   .collect(Collectors.toSet()),
                                 groups, User.class)
      .parallelStream()
      .collect(Collectors.toMap(user -> new UserPrototype(user.getFullName(), user.tryGetGroupTitle()),
                                user -> user,
                                (u1, u2) -> u1, HashMap::new));
  userRepository.saveAll(
                    achievementsByUser
                        .keySet()
                        .parallelStream()
                        .filter(user -> !existingUsersByPrototype.containsKey(user))
                        .sequential()
                        .map(userPrototype -> {
                          final String[] parts = new String[] { "", "", "" };
                          final String[] split = userPrototype.fullName.split(" ", 3);
                          if (split.length > 0) parts[0] = split[0];
                          if (split.length > 1) parts[1] = split[1];
                          if (split.length > 2) parts[2] = split[2];

                          return User.builder()
                                     .isNew(false)
                                     .state(ParticipantState.PARTICIPANT.name())
                                     .secondName(parts[0]).firstName(parts[1]).thirdName(parts[2])
                                     .group(existingGroupByTitle.get(userPrototype.group))
                                     .build();
                        }).toList())
                .forEach(user -> existingUsersByPrototype.put(
                    new UserPrototype(user.getFullName(), user.tryGetGroupTitle()),
                    user));

  achievementRepository.saveAll(
      achievementsByUser
          .entrySet()
          .stream()
          .flatMap(entry -> entry
              .getValue()
              .stream()
              .map(achievementPrototype ->
                       Achievement.builder()
                                  .comment(achievementPrototype.comment)
                                  .typeCode(achievementPrototype.typeCode.charAt(0) - '0')
                                  .criteriaTitle(achievementPrototype.criteriaTitle)
                                  .typeTitle(achievementPrototype.typeTitle)
                                  .description(achievementPrototype.description)
                                  .levelTitle(achievementPrototype.levelTitle)
                                  .statusTitle(achievementPrototype.statusTitle)
                                  .thanksFrom(achievementPrototype.thanksFrom)
                                  .totalScore(Float.parseFloat(achievementPrototype.total.replace(',', '.')))
                                  .owner(existingUsersByPrototype.get(entry.getKey()))
                                  .stage(stage)
                                  .build()))
          .toList());

  AchievementScoreTicker.addAllForEvaluation(existingUsersByPrototype.values(), stageId);

  return existingUsersByPrototype.values()
                                 .parallelStream()
                                 .filter(user -> user.getGroup() == null)
                                 .map(User::getFullName)
                                 .toList();
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public AchievementTableProj edit(final Long id, final AchievementDto dto) {
  final Achievement achievement = achievementRepository.save(
      achievementRepository.findById(id).orElseThrow().editFrom(dto));
  AchievementScoreTicker.addForEvaluation(achievement.getOwner().getId(), achievement.getStage().getId());
  return projectionFactory.createProjection(AchievementTableProj.class, achievement);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public void delete(final Long id) {
  final Achievement achievement = achievementRepository.findById(id).orElseThrow();
  AchievementScoreTicker.addForEvaluation(achievement.getOwner().getId(), achievement.getStage().getId());
  achievementRepository.deleteById(id);
}

private record UserPrototype(@NotNull String fullName, String group) {
  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final UserPrototype that = (UserPrototype) o;

    if (StringUtils.isEmpty(that.group) || StringUtils.isEmpty(group)) {
      return Objects.equals(fullName, that.fullName);
    } else {
      return Objects.equals(fullName, that.fullName) && Objects.equals(group, that.group);
    }
  }

  @Override
  public int hashCode() {
    return fullName.hashCode();
  }
}

private record AchievementPrototype(String comment, String typeCode,
                                    String criteriaTitle, String typeTitle,
                                    String description, String levelTitle,
                                    String statusTitle, String thanksFrom,
                                    String total) {}
}
