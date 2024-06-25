package ru.valkovets.mephisoty.db.service.userdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.dto.userdata.CredentialsDto;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;

@Service
public class CredentialsService {
private final CredentialsRepository credentialsRepository;
private final PasswordEncoder passwordEncoder;

public CredentialsService(final CredentialsRepository credentialsRepository, final PasswordEncoder passwordEncoder) {
    this.credentialsRepository = credentialsRepository;
    this.passwordEncoder = passwordEncoder;
}

public Credentials create(final CredentialsDto credentialsDto) {
    return credentialsRepository.save(Credentials.from(credentialsDto, passwordEncoder));
}
}
