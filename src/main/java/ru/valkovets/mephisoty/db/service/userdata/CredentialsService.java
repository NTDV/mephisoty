package ru.valkovets.mephisoty.db.service.userdata;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.model.userdata.User;
import ru.valkovets.mephisoty.db.projection.special.NamesProj;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;
import ru.valkovets.mephisoty.db.repository.userdata.UserRepository;

@Service
@RequiredArgsConstructor
public class CredentialsService {
private final UserRepository userRepository;
private final CredentialsRepository credentialsRepository;

public Credentials save(final SignUpRequest signUpRequest, final PasswordEncoder passwordEncoder) {
    final Credentials credentials = save(Credentials.from(signUpRequest, passwordEncoder));
    final User user = credentials.getUser();
    user.setCredentials(credentials);
    userRepository.save(user);
    return credentials;
}

public Credentials save(final Credentials credentials) {
    return credentialsRepository.save(credentials);
}

public Credentials getByEmail(final String email) {
    return credentialsRepository.findByEmail(email).orElseThrow();
}

public UserDetailsService userDetailsService() {
    return this::getByEmail;
}

public Credentials getCurrentFromDb() {
    return getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
}

@PreAuthorize("hasAuthority(T(ru.valkovets.mephisoty.settings.UserRole).ADMIN)")
public String getFullNameByCredentialsId(final Long id) {
    return userRepository.getFullNameByCredentialsId(id).orElse(NamesProj.EMPTY).getFullName();
}
}
