package ru.valkovets.mephisoty.application.lifecycle;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Configuration;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;
import ru.valkovets.mephisoty.settings.ParticipantState;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class Init implements SmartInitializingSingleton {
private final CredentialsRepository credentialsRepository;

@Override
public void afterSingletonsInstantiated() {
  if (true != false) return;

  if (!credentialsRepository.existsById(1)) {
    credentialsRepository.saveAll(
        List.of(
            Credentials
                .builder()
                .comment("Начальный администратор")
                .role(UserRole.ADMIN)
                .email("dahnh33@gmail.com")
                .mephiLogin("divalkovets")
                .mephiIsStudent(false)
                .user(User.builder()
                          .comment("Начальный администратор")
                          .firstName("Данила")
                          .secondName("Вальковец")
                          .thirdName("Игоревич")
                          .state(ParticipantState.NOT_PARTICIPANT.name())
                          .build())
                .build(),

            Credentials
                .builder()
                .comment("Начальный участник")
                .role(UserRole.PARTICIPANT)
                .email("dahnh33@gmail.com")
                .mephiLogin("vdi006")
                .mephiIsStudent(true)
                .user(User.builder()
                          .comment("Начальный участник")
                          .firstName("Данила")
                          .secondName("Вальковец")
                          .thirdName("Игоревич")
                          .state(ParticipantState.PARTICIPANT.name())
                          .build())
                .build()));
  }
}
}
