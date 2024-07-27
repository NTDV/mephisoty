package ru.valkovets.mephisoty.db.projection.special;

import ru.valkovets.mephisoty.db.projection.simple.UserSimpleGroupProj;
import ru.valkovets.mephisoty.db.projection.simple.UserSimpleProj;

import java.util.Objects;

public record CriteriaScoreRepositoryRecord(
    Long id,
    String comment,
    UserSimpleGroupProj participant,
    UserSimpleProj expert,
    Float score) implements CriteriaScoreProj {
@Override
public boolean equals(final Object obj) {
  if (this == obj) return true;
  if (!(obj instanceof final CriteriaScoreProj other)) return false;
  return Objects.equals(other.getId(), id);
}

@Override
public int hashCode() {
  return (int) (id % Integer.MAX_VALUE);
}

@Override
public UserSimpleGroupProj getParticipant() {
  return participant;
}

@Override
public UserSimpleProj getExpert() {
  return expert;
}

@Override
public Float getScore() {
  return score;
}

@Override
public String getComment() {
  return comment;
}

@Override
public Long getId() {
  return id;
}
}