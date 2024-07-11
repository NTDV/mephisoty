package ru.valkovets.mephisoty.db.projection.complex;

import ru.valkovets.mephisoty.db.model.superclass.TdrEntity;
import ru.valkovets.mephisoty.db.projection.simple.TitleProj;

/**
 * Projection for {@link TdrEntity}
 */
public interface TdrProj extends TitleProj {
String getDescription();

String getRules();
}