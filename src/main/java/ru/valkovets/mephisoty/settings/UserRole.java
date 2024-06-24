package ru.valkovets.mephisoty.settings;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    PARTICIPANT,
    EXPERT,
    ADMIN,
    DISABLED,
    BANNED;

@Override
public String getAuthority() {
    return this.name();
}
}
