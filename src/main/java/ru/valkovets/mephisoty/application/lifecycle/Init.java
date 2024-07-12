package ru.valkovets.mephisoty.application.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.api.dto.userdata.UserRegistrationDto;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;
import ru.valkovets.mephisoty.settings.ParticipantState;
import ru.valkovets.mephisoty.settings.UserRole;

@Configuration
@RequiredArgsConstructor
public class Init implements SmartInitializingSingleton {
private final PasswordEncoder passwordEncoder;
private final CredentialsRepository credentialsRepository;
private final CredentialsService credentialsService;

@Override
public void afterSingletonsInstantiated() {
  if (!credentialsRepository.existsById(1))
    System.out.println(credentialsService.save(
        new SignUpRequest("Начальный пользователь",
                          "dahnh33@gmail.com",
                          "11",
                          new UserRegistrationDto(
                              "Начальный пользователь",
                              ParticipantState.NOT_PARTICIPANT,
                              "Данила",
                              "Вальковец",
                              "Игоревич"),
                          UserRole.ADMIN),
        passwordEncoder).getId());
}
}
