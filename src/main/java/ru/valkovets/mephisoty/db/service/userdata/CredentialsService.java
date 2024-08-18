package ru.valkovets.mephisoty.db.service.userdata;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.special.NamesProj;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;
import ru.valkovets.mephisoty.security.credentials.CasUserXml;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CredentialsService {
private final UserRepository userRepository;
private final CredentialsRepository credentialsRepository;

public Credentials save(final SignUpRequest signUpRequest) {
    final Credentials credentials = save(Credentials.from(signUpRequest));
    final User user = credentials.getUser();
    user.setCredentials(credentials);
    userRepository.save(user);
    return credentials;
}

public Credentials save(final Credentials credentials) {
    return credentialsRepository.save(credentials);
}

@Transactional
public Optional<Credentials> findByMephiLogin(final String email) {
    return credentialsRepository.findByMephiLogin(email);
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public String getFullNameByCredentialsId(final Long id) {
    return userRepository.getFullNameByCredentialsId(id).orElse(NamesProj.EMPTY).getFullName();
}

@Transactional
public Credentials createNew(final CasUserXml mephiUser) {
    final CasUserXml.AuthenticationSuccess userData = mephiUser.getAuthenticationSuccess();
    if (userData == null) {
        return null;
    }

    final boolean isProbablyExpert = userData.getAttributes() == null;

    final Credentials credentials;
    final User innerUser;

    if (isProbablyExpert) {
        innerUser = User.newEpmty();
        credentials = Credentials.builder()
                                 .mephiIsStudent(false)
                                 .mephiLogin(userData.getMephiLogin())
                                 .role(UserRole.fromMephiLogin(userData.getMephiLogin(), true))
                                 .build();
    } else {
        credentials = Credentials
            .builder()
            .mephiIsStudent(true)
            .mephiLogin(userData.getMephiLogin())
            .role(UserRole.PARTICIPANT)
            .build();

        final Set<User> users = userRepository.findAllByFullNameAndWithoutCredentials(userData.getAttributes().getFullName());
        if (users.size() > 1) {
            innerUser = User.from(userData.getAttributes());
            credentials.setComment("[[Человек с таким ФИО уже был внесен в систему при регистрации данного пользователя]]");
        } else if (users.size() == 1) {
            innerUser = users.iterator().next();
        } else {
            innerUser = User.from(userData.getAttributes());
        }
    }

    innerUser.setCredentials(credentials);
    credentials.setUser(userRepository.save(innerUser)); // Because user is owner

    return credentials;
}
}
