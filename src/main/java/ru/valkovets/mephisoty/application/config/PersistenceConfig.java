package ru.valkovets.mephisoty.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.valkovets.mephisoty.security.audition.AuditorAwareImpl;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.valkovets.mephisoty.db.repository")
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class PersistenceConfig {
@Bean
AuditorAware<Long> auditorProvider() {
    return new AuditorAwareImpl();
}
}
