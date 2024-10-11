package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.ParticipantState;

import java.io.Serializable;
import java.util.List;

@Builder
public record ParticipantMeDto(
    Long avatarFileId,

    String firstName,
    String secondName,
    String thirdName,

    String fullName,
    String fio,

    String groupTitle,

    String vk,
    String tg,
    String phone,

    ParticipantState state,

    StageMeDto portfolioStage,
    List<StageMeDto> appliedStages,
    List<StageMeDto> appliedFinalStages,

    Long place,
    Long lastPosition,
    Float score,
    Float maximumScore,

    Long placeFinal,
    Long lastPositionFinal,
    Float scoreFinal,
    Float maximumScoreFinal
) implements Serializable {

public static ParticipantMeDto from(final @NotNull User participant, final @NotNull Long lastPosition,
                                    @Nullable final StageMeDto portfolioStage,
                                    final @NotNull List<StageMeDto> appliedStages,
                                    final @NotNull List<StageMeDto> appliedFinalStages,
                                    @Nullable final SeasonScore seasonScore, @Nullable final SeasonScore seasonFinalScore,
                                    final @NotNull Season season, final @NotNull Season seasonFinal) {
  return ParticipantMeDto
      .builder()
      .avatarFileId(participant.tryGetAvatarId())

      .firstName(participant.getFirstName())
      .secondName(participant.getSecondName())
      .thirdName(participant.getThirdName())

      .fullName(participant.getFullName().isBlank() ? null : participant.getFullName())
      .fio(participant.getFullName().isBlank() ?
           "ID: " + participant.getId().toString() :
           (participant.getSecondName() + " " + participant.getFirstName().charAt(0) + ". " +
            (participant.getThirdName().isEmpty() ? "" : participant.getThirdName().charAt(0) + ".")
           ).trim())

      .groupTitle(participant.getGroup() != null ? participant.getGroup().getTitle() : null)

      .vk(participant.getVkNick())
      .tg(participant.getTgNick())
      .phone(participant.getPhoneNumber())

      .state(participant.getState())

      .portfolioStage(portfolioStage)
      .appliedStages(appliedStages)
      .appliedFinalStages(appliedFinalStages)

      .place(
          season.getScoreVisibility().equals(AllowState.NO.name()) ? null : seasonScore != null ? seasonScore.getPlace() : 0)
      .score(season.getScoreVisibility().equals(AllowState.NO.name()) ? null : seasonScore != null
                                                                               ? seasonScore.getScoreBySeasonFormula() : 0)
      .lastPosition(lastPosition)
      .maximumScore(90f)

      .placeFinal(seasonFinal.getScoreVisibility().equals(AllowState.NO.name()) ? null : seasonFinalScore != null
                                                                                         ? seasonFinalScore.getPlace() : 0)
      .scoreFinal(seasonFinal.getScoreVisibility().equals(AllowState.NO.name()) ? null : seasonFinalScore != null
                                                                                         ? seasonFinalScore.getScoreBySeasonFormula()
                                                                                         : 0)
      .lastPositionFinal(100L)
      .maximumScoreFinal(100f)

      .build();
}
}
