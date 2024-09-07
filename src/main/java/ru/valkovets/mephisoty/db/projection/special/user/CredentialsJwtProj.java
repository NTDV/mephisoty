package ru.valkovets.mephisoty.db.projection.special.user;

import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link Credentials}
 */
public interface CredentialsJwtProj extends IdProj {
String getMephiLogin();

String getRole();

UserJwtProj getUser();
}