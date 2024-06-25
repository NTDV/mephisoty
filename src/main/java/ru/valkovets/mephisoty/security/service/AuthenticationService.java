package ru.valkovets.mephisoty.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.JwtAuthenticationResponse;
import ru.valkovets.mephisoty.api.dto.userdata.SignInRequest;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
private final CredentialsService credentialsService;
private final JwtService jwtService;
private final PasswordEncoder passwordEncoder;
private final AuthenticationManager authenticationManager;

/**
 * Регистрация пользователя
 *
 * @param request данные пользователя
 * @return токен
 */
public JwtAuthenticationResponse signUp(final SignUpRequest request) {
    return new JwtAuthenticationResponse(jwtService.generateToken(credentialsService.save(request, passwordEncoder)));
}

/**
 * Аутентификация пользователя
 *
 * @param request данные пользователя
 * @return токен
 */
public JwtAuthenticationResponse signIn(final SignInRequest request) {
    try {
        authenticationManager.authenticate(request.getAuthToken());
    } catch (final BadCredentialsException e) {
        System.out.println(e);
        throw e;
    }
    final UserDetails credentials = credentialsService
            .userDetailsService()
            .loadUserByUsername(request.email());
    return new JwtAuthenticationResponse(jwtService.generateToken(credentials));
}
}
