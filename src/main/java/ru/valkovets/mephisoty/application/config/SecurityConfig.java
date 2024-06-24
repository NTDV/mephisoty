package ru.valkovets.mephisoty.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(11);

@Bean
public PasswordEncoder encoder() {
    return passwordEncoder;
}
}
