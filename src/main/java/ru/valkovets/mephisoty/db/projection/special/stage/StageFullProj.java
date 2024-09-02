package ru.valkovets.mephisoty.db.projection.special.stage;

import ru.valkovets.mephisoty.db.model.season.Stage;
import ru.valkovets.mephisoty.db.projection.complex.CreatedModifiedProj;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.special.file.FileProj;

import java.util.Set;

/**
 * Projection for {@link Stage}
 */
public interface StageFullProj extends CreatedModifiedProj, StageCriteriasShortProj {
  IdTitleProj getSeason();

Set<FileProj> getFiles();
}