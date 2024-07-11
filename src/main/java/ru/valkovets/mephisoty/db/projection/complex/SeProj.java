package ru.valkovets.mephisoty.db.projection.complex;

import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;

import java.time.OffsetDateTime;

/**
 * Projection for {@link TdrseEntity}
 */
public interface SeProj {
OffsetDateTime getStart();
OffsetDateTime getEnd();
}