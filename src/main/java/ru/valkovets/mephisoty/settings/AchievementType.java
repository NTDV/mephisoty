package ru.valkovets.mephisoty.settings;

public enum AchievementType {
  ALL,

  STUDYING,
  SCIENCE,
  SOCIAL,
  SPORT,
  CULTURE;

public static final AchievementType[] values = values();

public static AchievementType getFrom(final String name) {
  final int type = (name.charAt(0) - '0');
  return type >= 0 && type < values.length ? values[type] : null;
}
}
