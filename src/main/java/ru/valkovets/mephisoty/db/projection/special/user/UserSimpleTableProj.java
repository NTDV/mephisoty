package ru.valkovets.mephisoty.db.projection.special.user;

import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link User}
 */
public interface UserSimpleTableProj extends IdCommentProj {
String getFirstName();

String getSecondName();

String getThirdName();

IdTitleProj getGroup();

String getState();

String getVkNick();

String getTgNick();

String getPhoneNumber();
}