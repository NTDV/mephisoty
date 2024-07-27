package ru.valkovets.mephisoty.db.projection.simple;

import ru.valkovets.mephisoty.db.model.userdata.User;

/**
 * Projection for {@link User}
 */
public interface UserSimpleProj extends IdProj {
String getFirstName();

String getSecondName();

String getThirdName();
}