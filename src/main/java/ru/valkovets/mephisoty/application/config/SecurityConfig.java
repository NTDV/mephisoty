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
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
      }))
      .authorizeHttpRequests(request -> request
          .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
          // * - 1 уровень вложенности, ** - любое количество уровней вложенности
          .requestMatchers("/admin/**", "/file/all", "creds/**").hasAuthority(UserRole.ADMIN.getAuthority())
          .requestMatchers("/expert/**").hasAnyAuthority(UserRole.ADMIN.getAuthority(), UserRole.EXPERT.getAuthority())
          .requestMatchers("/participant/**")
          .hasAnyAuthority(UserRole.ADMIN.getAuthority(), UserRole.PARTICIPANT.getAuthority())
          .requestMatchers("/public/**", "/auth/login", "/file/public/*", "/swagger-ui/**", "/swagger-resources/**",
                           "/v3/api-docs/**").permitAll()
          .requestMatchers("/file/*")
          .hasAnyAuthority(UserRole.ADMIN.getAuthority(), UserRole.EXPERT.getAuthority(), UserRole.PARTICIPANT.getAuthority())
          .anyRequest().denyAll())
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