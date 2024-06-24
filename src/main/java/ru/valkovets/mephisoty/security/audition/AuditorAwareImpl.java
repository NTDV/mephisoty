package ru.valkovets.mephisoty.security.audition;

import org.springframework.data.domain.AuditorAware;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Long> {

@Override
public Optional<Long> getCurrentAuditor() {
    try {
        final Credentials credentials = Credentials.getCurrent();
        if (credentials == null || credentials.getId() == 0) return Optional.empty();
        else return Optional.of(credentials.getId());
    } catch (final Exception e) {
        return Optional.empty();
    }
}
}