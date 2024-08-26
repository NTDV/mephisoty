package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.userdata.User;
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

public static ParticipantMeDto from(@NotNull final User participant, @Nullable final StageMeDto portfolioStage,
                                    @NotNull final List<StageMeDto> appliedStages,
                                    @NotNull final List<StageMeDto> appliedFinalStages,
                                    @Nullable final SeasonScore seasonScore, @Nullable final SeasonScore seasonFinalScore,
                                    @NotNull final Season season, @NotNull final Season seasonFinal) {
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

      .place(seasonScore != null ? seasonScore.getPlace() : 0)
      .score(seasonScore != null ? seasonScore.getScoreBySeasonFormula() : 0)
      .lastPosition(season.getLastPosition())
      .maximumScore(season.getMaximumScore())

      .placeFinal(seasonFinalScore != null ? seasonFinalScore.getPlace() : 0)
      .scoreFinal(seasonFinalScore != null ? seasonFinalScore.getScoreBySeasonFormula() : 0)
      .lastPositionFinal(seasonFinal.getLastPosition())
      .maximumScoreFinal(seasonFinal.getMaximumScore())

      .build();
}
}
