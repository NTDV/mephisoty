package ru.valkovets.mephisoty.db.projection.special.user;

import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link User}
 */
public interface UserJwtProj extends IdProj {
//IdTitleProj getAvatar();
String getFirstName();

String getSecondName();

String getThirdName();

Boolean getIsNew();

IdTitleProj getGroup();

String getState();
}