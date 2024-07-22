package ru.valkovets.mephisoty.db.projection.complex;

import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;
import ru.valkovets.mephisoty.db.projection.simple.CommentProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link BasicEntity}
 */
public interface IdCommentProj extends IdProj, CommentProj {
}