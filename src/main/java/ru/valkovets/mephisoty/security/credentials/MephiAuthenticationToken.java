package ru.valkovets.mephisoty.security.credentials;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;

@Getter
public class MephiAuthenticationToken extends AbstractAuthenticationToken {
private final String mephiLogin;
private final CasUserXml mephiUser;
private final Credentials dbCredentials;

public MephiAuthenticationToken(final CasUserXml mephiUser) {
  super(null);

  this.mephiLogin = mephiUser.getAuthenticationSuccess().getMephiLogin();
  this.mephiUser = mephiUser;
  this.dbCredentials = null;

  setAuthenticated(false);
}

public MephiAuthenticationToken(final Credentials dbCredentials) {
  super(dbCredentials.getAuthorities());

  this.mephiLogin = dbCredentials.getMephiLogin();
  this.mephiUser = null;
  this.dbCredentials = dbCredentials;

  setAuthenticated(true);
}

public Object getCredentials() {
  return mephiLogin;
}

@Override
public Object getPrincipal() {
  return dbCredentials;
}
}
