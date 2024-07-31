package ru.valkovets.mephisoty.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.valkovets.mephisoty.api.dto.userdata.JwtAuthenticationResponse;
import ru.valkovets.mephisoty.api.dto.userdata.SignInRequest;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.security.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
private final AuthenticationService authenticationService;

@Operation(summary = "Регистрация пользователя")
@PostMapping("/register")
public JwtAuthenticationResponse register(@RequestBody @Valid final SignUpRequest request) {
    return authenticationService.register(request);
}

@Operation(summary = "Авторизация пользователя")
@PostMapping("/login")
public JwtAuthenticationResponse login(@RequestBody @Valid final SignInRequest request) {
    return authenticationService.login(request);
}
}
