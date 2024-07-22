package ru.valkovets.mephisoty.db.projection.extended;

import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.db.projection.complex.TdrProj;
import ru.valkovets.mephisoty.db.projection.simple.CommentProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link TdrEntity}
 */
public interface IdTdrCommentProj extends IdProj, TdrProj, CommentProj {
}