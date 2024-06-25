package ru.valkovets.mephisoty.db.service.userdata;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.SignUpRequest;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;

@Service
public class CredentialsService {
private final CredentialsRepository credentialsRepository;

public CredentialsService(final CredentialsRepository credentialsRepository) {
    this.credentialsRepository = credentialsRepository;
}

public Credentials save(final SignUpRequest signUpRequest, final PasswordEncoder passwordEncoder) {
    return save(Credentials.from(signUpRequest, passwordEncoder));
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
}
