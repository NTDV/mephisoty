package ru.valkovets.mephisoty.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;
import ru.valkovets.mephisoty.db.service.userdata.CredentialsService;
import ru.valkovets.mephisoty.security.credentials.MephiAuthenticationToken;
import ru.valkovets.mephisoty.security.service.JwtService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
public static final String BEARER_PREFIX = "Bearer ";
public static final String HEADER_NAME = "Authorization";
private final JwtService jwtService;
private final CredentialsService credentialsService;

@Override
protected boolean shouldNotFilter(final HttpServletRequest request) {
  final String servletPath = request.getServletPath();
  return "GET".equals(request.getMethod()) && (
      !servletPath.startsWith("/api") ||

      servletPath.startsWith("/api/auth/login") ||
      servletPath.startsWith("/api/public/") ||
      servletPath.startsWith("/api/file/public/") ||

      servletPath.startsWith("/api/swagger-ui/") ||
      servletPath.startsWith("/api/swagger-resources/") ||
      servletPath.startsWith("/api/v3/api-docs/"));
}

@Override
protected void doFilterInternal(
    final @NonNull HttpServletRequest request,
    final @NonNull HttpServletResponse response,
    final @NonNull FilterChain filterChain) throws ServletException, IOException {
  try {
    // Получаем токен из заголовка
    final String authHeader = request.getHeader(HEADER_NAME);
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    // Обрезаем префикс и получаем имя пользователя из токена
    final String jwt = authHeader.substring(BEARER_PREFIX.length());
    final String username = jwtService.extractUserName(jwt);

    if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
      final Credentials credentials = credentialsService.findByMephiLogin(username)
                                                        .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(
                                                            "User not found"));

      // Если токен валиден, то аутентифицируем пользователя
      if (jwtService.isTokenValid(jwt, credentials)) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        final MephiAuthenticationToken authToken = new MephiAuthenticationToken(credentials);

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        context.setAuthentication(authToken);
        SecurityContextHolder.setContext(context);
      }
    }

    filterChain.doFilter(request, response);
  } catch (final ExpiredJwtException expiredJwtException) {
    response.getWriter().write("{\"err\":\"ExpiredJwtException\", \"dsc\":\"\"}");
  }
}
}
