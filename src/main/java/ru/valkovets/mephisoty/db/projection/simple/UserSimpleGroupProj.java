package ru.valkovets.mephisoty.db.projection.simple;

import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link User}
 */
public interface UserSimpleGroupProj extends UserSimpleProj {
IdTitleProj getGroup();
}