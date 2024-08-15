package ru.valkovets.mephisoty.db.projection.simple;

import jakarta.annotation.Nullable;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.extended.IdTitleProj;

/**
 * Projection for {@link User}
 */
public interface UserSelectProj extends UserNameIdProj {
@Nullable
IdTitleProj getGroup();
}