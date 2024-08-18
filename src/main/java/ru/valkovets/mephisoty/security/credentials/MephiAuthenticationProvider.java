package ru.valkovets.mephisoty.security.credentials;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;

public class MephiAuthenticationProvider implements AuthenticationProvider {
private final CredentialsService credentialsService;

public MephiAuthenticationProvider(final CredentialsService credentialsService) {
  this.credentialsService = credentialsService;
}

@Override
@Transactional
public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
  Assert.isInstanceOf(MephiAuthenticationToken.class, authentication, "Only MephiAuthenticationToken is supported");

  final MephiAuthenticationToken tokenAuth = (MephiAuthenticationToken) authentication;
  final String mephiLogin = determineUsername(tokenAuth);
  if (mephiLogin == null || mephiLogin.isBlank()) {
    throw new BadCredentialsException("Can not determine username");
  }

  final Credentials credentials = credentialsService
      .findByMephiLogin(mephiLogin)
      .orElseGet(() -> credentialsService.createNew(tokenAuth.getMephiUser()));

  if (credentials == null || credentials.getId() == null) {
    throw new BadCredentialsException("User not found");
  }

  return new MephiAuthenticationToken(credentials);
}

private static String determineUsername(final MephiAuthenticationToken tokenAuth) {
  return (tokenAuth.getMephiUser() == null) ? null : tokenAuth.getMephiLogin();
}

@Override
public boolean supports(final Class<?> authentication) {
  return MephiAuthenticationToken.class.isAssignableFrom(authentication);
}
}
