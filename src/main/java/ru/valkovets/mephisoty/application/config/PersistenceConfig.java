package ru.valkovets.mephisoty.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ru.valkovets.mephisoty.security.audition.AuditorAwareImpl;

@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class PersistenceConfig {
@Bean
AuditorAware<Long> auditorProvider() {
    return new AuditorAwareImpl();
}
}
