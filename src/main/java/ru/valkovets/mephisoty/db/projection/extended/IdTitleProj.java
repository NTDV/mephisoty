package ru.valkovets.mephisoty.db.projection.extended;

import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;
import ru.valkovets.mephisoty.db.projection.simple.TitleProj;

/**
 * Projection for {@link TdrEntity}
 */
public interface IdTitleProj extends IdProj, TitleProj {
}