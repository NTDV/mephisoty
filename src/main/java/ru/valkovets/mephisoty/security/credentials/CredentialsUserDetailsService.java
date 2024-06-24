package ru.valkovets.mephisoty.security.credentials;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.repository.userdata.CredentialsRepository;

@Service
public class CredentialsUserDetailsService implements UserDetailsService {
private final CredentialsRepository credentialsRepository;

public CredentialsUserDetailsService(final CredentialsRepository credentialsRepository) {
    this.credentialsRepository = credentialsRepository;
}

@Override
public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    return credentialsRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
}
}
