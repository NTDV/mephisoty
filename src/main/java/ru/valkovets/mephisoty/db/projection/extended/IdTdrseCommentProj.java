package ru.valkovets.mephisoty.db.projection.extended;

import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.db.projection.complex.SeProj;
import ru.valkovets.mephisoty.db.projection.complex.TdrProj;
import ru.valkovets.mephisoty.db.projection.simple.CommentProj;
import ru.valkovets.mephisoty.db.projection.simple.IdProj;

/**
 * Projection for {@link TdrseEntity}
 */
public interface IdTdrseCommentProj extends IdProj, TdrProj, SeProj, CommentProj {
}