package ru.valkovets.mephisoty.db.projection.special.user;

import ru.valkovets.mephisoty.db.projection.simple.IdProj;
import ru.valkovets.mephisoty.settings.UserRole;

/**
 * Projection for {@link ru.valkovets.mephisoty.db.model.userdata.Credentials}
 */
public interface CredentialsTableProj extends IdProj {
String getMephiLogin();

UserRole getRole();
}