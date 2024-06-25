package ru.valkovets.mephisoty.api;

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
public class AuthController {
private final AuthenticationService authenticationService;

//@Operation(summary = "Регистрация пользователя")
@PostMapping("/sign-up")
public JwtAuthenticationResponse signUp(@RequestBody @Valid final SignUpRequest request) {
    return authenticationService.signUp(request);
}

//@Operation(summary = "Авторизация пользователя")
@PostMapping("/sign-in")
public JwtAuthenticationResponse signIn(@RequestBody @Valid final SignInRequest request) {
    return authenticationService.signIn(request);
}
}
