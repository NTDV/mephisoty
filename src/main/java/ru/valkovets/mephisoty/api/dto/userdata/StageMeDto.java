package ru.valkovets.mephisoty.api.dto.userdata;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.db.model.files.File;
import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.model.season.scoring.StageScore;

import java.io.Serializable;

public record StageMeDto(
    String title,
    @Nullable Long protocolFileId,
    @Nullable Float score
)
    implements Serializable {
public static StageMeDto from(final StageScore stageScore) {
  final Stage stage = stageScore.getStage();

  return new StageMeDto(
      stage.getTitle(),
      stage.getFiles()
           .stream()
           .filter(file -> "protocol".equals(file.getCode()))
           .map(File::getId)
           .findFirst()
           .orElse(null),
      stageScore.getScoreByStageFormula()
  );
}
}
