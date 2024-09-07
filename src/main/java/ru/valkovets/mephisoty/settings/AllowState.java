package ru.valkovets.mephisoty.settings;

import org.springframework.security.access.AccessDeniedException;

public enum AllowState {
    DISALLOW_ALL_FOR_PARTICIPANTS,
    ALLOW_READ_FOR_PARTICIPANTS,
    ALLOW_CREATE_READ_FOR_PARTICIPANTS,
    ALLOW_CREATE_EDIT_READ_FOR_PARTICIPANTS;

public static final AllowState YES = ALLOW_READ_FOR_PARTICIPANTS;
public static final AllowState NO = DISALLOW_ALL_FOR_PARTICIPANTS;

public boolean equals(final String string) {
  return name().equals(string);
}

public boolean isCanEdit() throws AccessDeniedException, IllegalStateException {
  if (this.equals(AllowState.DISALLOW_ALL_FOR_PARTICIPANTS) ||
      this.equals(AllowState.ALLOW_READ_FOR_PARTICIPANTS)) {
    throw new AccessDeniedException("Нельзя выбрать дату");
  } else if (this.equals(AllowState.ALLOW_CREATE_READ_FOR_PARTICIPANTS)) {
    return false;
  } else if (this.equals(AllowState.ALLOW_CREATE_EDIT_READ_FOR_PARTICIPANTS)) {
    return true;
  } else {
    throw new IllegalStateException("Unknown scheduleAccess: " + this.toString());
  }
}
}