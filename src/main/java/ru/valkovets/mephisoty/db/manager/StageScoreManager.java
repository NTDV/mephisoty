package ru.valkovets.mephisoty.db.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.valkovets.mephisoty.application.lifecycle.Init;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.Stage_;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore_;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.repository.UtilsRepository;
import ru.valkovets.mephisoty.db.repository.season.scoring.StageScoreRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StageScoreManager {
private static final int READ_BATCH_SIZE = 32;
private final StageScoreRepository stageScoreRepository;

private final StageScoreManager_2 stageScoreManager_2;

@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
public void evaluateStageScore(final Collection<Long> stageIds) {
  stageScoreRepository.deleteAll(stageScoreRepository.getAllByStage_IdIn(stageIds, StageScore.class));

  TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override
    public void afterCommit() {
      stageScoreManager_2.evaluateStageScore_2(stageIds);
    }
  });
}

@RequiredArgsConstructor
@Service
public static class StageScoreManager_2 {
  private final UserRepository userRepository;
  private final StageScoreRepository stageScoreRepository;
  private final UtilsRepository utilsRepository;

  private final StageScoreManager_3 stageScoreManager_3;

  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
  public void evaluateStageScore_2(final Collection<Long> stageIds) {
    final List<Stage> stages = utilsRepository.findAllByIdIn(stageIds, Stage.class);

    Pageable userPageable = Pageable.ofSize(READ_BATCH_SIZE);
    Page<User> users;

    do {
      users = userRepository.findAllByChosenStagesIdInOrderById(stageIds, userPageable, User.class);

      for (final User user : users) {
        for (final Stage stage : stages) {
          if (!user.getChosenStages().contains(stage)) continue;

          stageScoreRepository.save(
              StageScore
                  .builder()
                  .participant(user)
                  .stage(stage)
                  .initialScore((float) user
                      .getCriteriaScores()
                      .stream()
                      .filter(criteriaScore -> criteriaScore.getCriteria().getStage().getId()
                                                            .equals(stage.getId()))
                      .map(score -> new AbstractMap.SimpleEntry<>(score.getExpert().getId(),
                                                                  score.getScore()))
                      .collect(
                          Collectors.groupingBy(Map.Entry::getKey,
                                                Collectors.mapping(Map.Entry::getValue,
                                                                   Collectors.toList())))
                      .values()
                      .stream()
                      .mapToDouble(scores -> scores.stream().mapToDouble(Float::doubleValue).sum())
                      .average().orElse(0))
                  .build());
        }
      }

      userPageable = users.nextPageable();
    } while (users.hasNext());

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        stageScoreManager_3.evaluateStageScore_3(stageIds);
      }
    });
  }
}

@RequiredArgsConstructor
@Service
public static class StageScoreManager_3 {
  private final UserRepository userRepository;
  private final StageScoreRepository stageScoreRepository;
  private final UtilsRepository utilsRepository;

  private final StageScoreManager_4 stageScoreManager_4;

  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
  public void evaluateStageScore_3(final Collection<Long> stageIds) {
    final List<Stage> stages = utilsRepository.findAllByIdIn(stageIds, Stage.class);

    Pageable userPageable = Pageable.ofSize(READ_BATCH_SIZE);
    Page<User> users;

    final Map<Long, Float> maxInitialScores = new HashMap<>(stageIds.size());
    for (final Long stageId : stageIds) {
      maxInitialScores.put(stageId, stageScoreRepository.getMaxInitialScoreByStage_Id(stageId));
    }

    final Map<Long, Float> minInitialScores = new HashMap<>(stageIds.size());
    for (final Long stageId : stageIds) {
      minInitialScores.put(stageId, stageScoreRepository.getMinNotZeroInitialScoreByStage_Id(stageId));
    }


    do {
      users = userRepository.findAllByChosenStagesIdInOrderById(stageIds, userPageable, User.class);

      for (final User user : users) {
        for (final Stage stage : stages) {
          if (!user.getChosenStages().contains(stage)) continue;

          final Float max = maxInitialScores.get(stage.getId());
          final Float min = minInitialScores.get(stage.getId());

          final StageScore stageScore = stageScoreRepository
              .findByStage_IdAndParticipant_Id(stage.getId(), user.getId())
              .orElse(StageScore.builder().stage(stage).participant(user).initialScore(0f).build());

          if (Objects.equals(stage.getId(), Init._2024_WIREPARK_STAGE_ID)) {
            if (min == null || stageScore.getInitialScore() == null || stageScore.getInitialScore() == 0) {
              stageScore.setScoreByStageFormula(0f);
            } else {
              stageScore.setScoreByStageFormula((float) min / stageScore.getInitialScore() * 10f);
            }
          } else {
            if (max == null || stageScore.getInitialScore() == null || stageScore.getInitialScore() == 0) {
              stageScore.setScoreByStageFormula(0f);
            } else {
              stageScore.setScoreByStageFormula((float) stageScore.getInitialScore() / max * 10f);
            }
          }

          stageScoreRepository.save(stageScore);
        }
      }

      userPageable = users.nextPageable();
    } while (users.hasNext());

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        stageScoreManager_4.evaluateStageScore_4(stageIds);
      }
    });
  }
}

@RequiredArgsConstructor
@Service
public static class StageScoreManager_4 {
  private final StageScoreRepository stageScoreRepository;
  private final UtilsRepository utilsRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
  public void evaluateStageScore_4(final Collection<Long> stageIds) {
    final List<Stage> stages = utilsRepository.findAllByIdIn(stageIds, Stage.class);

    for (final Stage stage : stages) {
      Pageable scorePageable = Pageable.ofSize(READ_BATCH_SIZE);
      Page<StageScore> scores;

      long placer = 0;
      float lastScore = 11;

      do {
        final Pageable finalScorePageable = scorePageable;
        scores = stageScoreRepository.findBy(
            Specification.where(
                (root, query, builder) -> builder.equal(root.get(StageScore_.stage).get(Stage_.id), stage.getId())),
            q -> q.sortBy(Sort.by(Sort.Direction.DESC, StageScore_.SCORE_BY_STAGE_FORMULA)
                              .and(Sort.by(Sort.Direction.ASC, StageScore_.ID)))
                  .page(finalScorePageable));

        for (final StageScore score : scores) {

          if (Math.abs(lastScore - score.getScoreByStageFormula()) >= 0.01) {
            lastScore = score.getScoreByStageFormula();
            score.setPlace(++placer);
          } else {
            score.setPlace(placer);
          }

          stageScoreRepository.save(score);
        }

        scorePageable = scores.nextPageable();
      } while (scores.hasNext());
    }
  }
}
}