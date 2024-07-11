package ru.valkovets.mephisoty.db.projection.complex;

import ru.valkovets.mephisoty.db.model.superclass.BasicEntity;

import java.time.Instant;

/**
 * Projection for {@link BasicEntity}
 */
public interface CreatedModifiedProj {
Instant getCreatedAt();

Instant getModifiedAt();

Long getCreatedBy();

Long getModifiedBy();
}