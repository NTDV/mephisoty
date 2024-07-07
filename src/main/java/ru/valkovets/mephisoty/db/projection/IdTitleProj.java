package ru.valkovets.mephisoty.db.projection;

import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;

/**
 * Projection for {@link TdrEntity}
 */
public interface IdTitleProj {
Long getId();

String getTitle();
}