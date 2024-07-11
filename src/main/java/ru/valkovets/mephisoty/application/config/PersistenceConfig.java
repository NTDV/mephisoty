package ru.valkovets.mephisoty.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.valkovets.mephisoty.security.audition.AuditorAwareImpl;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.valkovets.mephisoty.db.repository")
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
public class PersistenceConfig {
private final SpelAwareProxyProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();
private final AuditorAwareImpl auditorAware = new AuditorAwareImpl();

@Bean
AuditorAware<Long> auditorProvider() {
    return auditorAware;
}

@Bean
public SpelAwareProxyProjectionFactory projectionFactory() {
    return projectionFactory;
}

//@Bean
//public Module hibernate6Module() {
//    return new Hibernate6Module().disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
//}
}
