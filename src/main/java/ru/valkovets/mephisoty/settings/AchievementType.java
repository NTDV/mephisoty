package ru.valkovets.mephisoty.settings;

public enum AchievementType {
  TOTAL,

  STUDYING,
  SCIENCE,
  SOCIAL,
  SPORT,
  CULTURE;

public static final AchievementType[] _VALUES = values();

public static AchievementType getFrom(final String name) {
  final int type = (name.charAt(0) - '0');
  return type >= 0 && type < _VALUES.length ? _VALUES[type] : null;
}
}
