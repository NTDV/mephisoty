package ru.valkovets.mephisoty.settings;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum UserRole implements GrantedAuthority {
    PARTICIPANT,
    EXPERT,
    ADMIN,
    DISABLED,
    BANNED;

private static final Set<String> ADMIN_MEPHI_LOGINS = Set.of("divalkovets",
                                                             "agborodina",
                                                             "amkhasanova");

@Override
public String getAuthority() {
    return this.name();
}

public static UserRole fromMephiLogin(final String login, final boolean isProbablyExpert) {
  if (!isProbablyExpert) return UserRole.PARTICIPANT;

  if (ADMIN_MEPHI_LOGINS.contains(login)) return UserRole.ADMIN;

  return UserRole.EXPERT;
}
}
