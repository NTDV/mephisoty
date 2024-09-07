package ru.valkovets.mephisoty.db.projection.special.user;

import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.complex.IdCommentProj;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

import java.util.List;

/**
 * Projection for {@link User}
 */
public interface UserTableProj extends IdCommentProj {
IdProj getAvatar();

String getFullName();

IdTitleProj getGroup();

CredentialsTableProj getCredentials();

String getState();

String getVkNick();

String getTgNick();

String getPhoneNumber();

List<IdTitleProj> getChosenStages();
}