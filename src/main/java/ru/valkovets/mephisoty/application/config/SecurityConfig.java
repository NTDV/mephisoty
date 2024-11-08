package ru.valkovets.mephisoty.application.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;
import ru.valkovets.mephisoty.security.credentials.MephiAuthenticationProvider;
import ru.valkovets.mephisoty.security.filter.JwtAuthenticationFilter;
import ru.valkovets.mephisoty.settings.UserRole;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
private final JwtAuthenticationFilter jwtAuthenticationFilter;
private final CredentialsService credentialsService;

@Bean
public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
  http.csrf(AbstractHttpConfigurer::disable)
      // Своего рода отключение CORS (разрешение запросов со всех доменов)
      .cors(cors -> cors.configurationSource(request -> {
        final var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("https://beststudents.mephi.ru", "http://localhost:5173"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
      }))
      .authorizeHttpRequests(request -> request
          .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
          // * - 1 уровень вложенности, ** - любое количество уровней вложенности
          .requestMatchers("/api/admin/**", "/api/file/all", "/api/creds/**").hasAuthority(UserRole.ADMIN.getAuthority())
          .requestMatchers("/api/expert/**").hasAnyAuthority(UserRole.ADMIN.getAuthority(), UserRole.EXPERT.getAuthority())
          .requestMatchers("/api/participant/**")
          .hasAnyAuthority(UserRole.ADMIN.getAuthority(), UserRole.PARTICIPANT.getAuthority())
          .requestMatchers("/api/public/**", "/api/auth/login", "/api/file/public/*", "/api/swagger-ui/**",
                           "/api/swagger-resources/**",
                           "/api/v3/api-docs/**").permitAll()
          .requestMatchers("/api/file/*").hasAnyAuthority(UserRole.ADMIN.getAuthority(), UserRole.EXPERT.getAuthority(),
                                                          UserRole.PARTICIPANT.getAuthority())
          .requestMatchers("/api/**").denyAll()
          .requestMatchers("/**").permitAll())
      .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
      .authenticationProvider(authenticationProvider(credentialsService))
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  return http.build();
}

@Bean
public AuthenticationProvider authenticationProvider(final CredentialsService credentialsService) {
  return new MephiAuthenticationProvider(credentialsService);
}

@Bean
public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
  return config.getAuthenticationManager();
}
}