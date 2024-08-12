package ru.valkovets.mephisoty.db.service.season.scoring.portfolio;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.CsvUploadDto;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.portfolio.Achievement;
import ru.valkovets.mephisoty.db.model.userdata.Group;
import ru.valkovets.mephisoty.db.model.userdata.User;
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
private final GroupRepository groupRepository;
private final UserRepository userRepository;
private final AchievementRepository achievementRepository;
private final StageRepository stageRepository;

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
@Transactional
public List<String> importNew(@NotNull final CsvUploadDto file, final Long stageId) throws IOException {
  final Stage stage = stageRepository.findById(stageId).orElseThrow();
  final HashSet<String> groups = new HashSet<>(64);
  final HashMap<UserPrototype, Set<AchievementPrototype>> achievementsByUser = new HashMap<>(256);

  (file.hasHeader() != null && file.hasHeader() ?
   CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true) :
   CSVFormat.DEFAULT.builder())
      .setDelimiter(file.delimiter() == null ? ";" : file.delimiter())
      .setAllowMissingColumnNames(true)
      .build()
      .parse(new BufferedReader(new InputStreamReader(file.file().getInputStream(), StandardCharsets.UTF_8)))
      .stream()
      .forEach(row -> {
        final String fullName = row.get(1).trim(); // Иванов Иван Иванович
        final String groupTitle = row.get(2).trim(); // Б22-534
        final String comment =
            row.get(17).trim(); // [пусто] не выполнены критерии успеваемости (есть "3"/пересдачи вне сессии)
        final String typeCode = row.get(19).trim(); // 1-Учеба
        final String criteriaTitle = row.get(20)
                                        .trim(); // Участие/победа в олимпиадах, конкурсах, соревнованиях,  турнирах, конкурсах профессионального и  инженерного мастерства
        final String typeTitle = row.get(21)
                                    .trim(); // Участие/победа в олимпиадах, интеллектуальных состязаниях, турнирах, конкурсах профессионального и инженерного мастерства
        final String description = row.get(22)
                                      .trim(); // Олимпиада "Я-профессионал" Экономика - Участник. Без указания места/степени. Международный уровень. Даты(а): 2023-12-27 - 2023-12-27. Дата: 2023, Ноябрь.
        final String levelTitle = row.get(26).trim(); // [пусто] Всероссийский
        final String statusTitle = row.get(27).trim(); // [пусто] Участник
        final String total = row.get(31).trim().replace(',', '.'); // 12,1

        groups.add(groupTitle);
        achievementsByUser.computeIfAbsent(new UserPrototype(fullName, groupTitle), k -> new HashSet<>(8))
                          .add(new AchievementPrototype(comment, typeCode, criteriaTitle, typeTitle,
                                                        description, levelTitle, statusTitle, total));
      });

  final HashMap<String, Group> existingGroupByTitle = groupRepository
      .findByTitleIn(groups, Group.class)
      .stream()
      .collect(Collectors.toMap(Group::getTitle, group -> group, (g1, g2) -> g1, HashMap::new));
  groups.removeAll(existingGroupByTitle.keySet());
  groupRepository.saveAll(groups.stream().map(title -> Group.builder().title(title).build()).toList())
                 .forEach(group -> existingGroupByTitle.put(group.getTitle(), group));

  final HashMap<UserPrototype, User> existingUsersByPrototype = userRepository
      .findAllByFullNameAndGroup(achievementsByUser.keySet().stream().map(UserPrototype::fullName).collect(Collectors.toSet()),
                                 groups, User.class)
      .stream()
      .collect(Collectors.toMap(user -> new UserPrototype(user.getFullName(), user.getGroup().getTitle()), user -> user,
                                (u1, u2) -> u1, HashMap::new)); // todo fix group can be null if user exists without group
  userRepository.saveAll(achievementsByUser.keySet().stream().filter(user -> !existingUsersByPrototype.containsKey(user))
                                           .map(userPrototype -> {
                                             final String[] parts = new String[] { "", "", "" };
                                             final String[] split = userPrototype.fullName.split(" ", 3);
                                             parts[0] = split[0];
                                             if (split.length > 1) parts[1] = split[1];
                                             if (split.length > 2) parts[2] = split[2];

                                             return User.builder()
                                                        .state(ParticipantState.PARTICIPANT.name())
                                                        .secondName(parts[0]).firstName(parts[1]).thirdName(parts[2])
                                                        .group(existingGroupByTitle.get(userPrototype.group))
                                                        .build();
                                           }).toList())
                .forEach(
                    user -> existingUsersByPrototype.put(new UserPrototype(user.getFullName(), user.getGroup().getTitle()),
                                                         user));


  achievementRepository.saveAll(
      achievementsByUser.entrySet()
                        .stream()
                        .flatMap(entry -> entry
                            .getValue()
                            .stream()
                            .map(achievementPrototype -> Achievement.builder()
                                                                    .comment(achievementPrototype.comment)
                                                                    .typeCode(achievementPrototype.typeCode.charAt(0) - '0')
                                                                    .criteriaTitle(achievementPrototype.criteriaTitle)
                                                                    .typeTitle(achievementPrototype.typeTitle)
                                                                    .description(achievementPrototype.description)
                                                                    .levelTitle(achievementPrototype.levelTitle)
                                                                    .statusTitle(achievementPrototype.statusTitle)
                                                                    .totalScore(Float.parseFloat(achievementPrototype.total))
                                                                    .owner(existingUsersByPrototype.get(entry.getKey()))
                                                                    .stage(stage)
                                                                    .build()))
                        .toList());
  return Collections.EMPTY_LIST;
}

public record UserPrototype(String fullName, String group) {}

private record AchievementPrototype(String comment, String typeCode, String criteriaTitle, String typeTitle,
                                    String description, String levelTitle, String statusTitle, String total) {}
}
