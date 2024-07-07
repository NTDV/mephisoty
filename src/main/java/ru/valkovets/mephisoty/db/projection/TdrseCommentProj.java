package ru.valkovets.mephisoty.db.projection;

import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;

import java.time.OffsetDateTime;

/**
 * Projection for {@link TdrseEntity}
 */
public interface TdrseCommentProj {
Long getId();

String getComment();

String getTitle();

String getDescription();

String getRules();

OffsetDateTime getStart();

OffsetDateTime getEnd();
}