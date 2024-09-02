package ru.valkovets.mephisoty.security.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.api.dto.userdata.JwtAuthenticationResponse;
import ru.valkovets.mephisoty.security.credentials.CasUserXml;
import ru.valkovets.mephisoty.security.credentials.MephiAuthenticationToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
private final JwtService jwtService;
private final AuthenticationManager authenticationManager;

private static final XmlMapper XML_MAPPER = new XmlMapper();

//public JwtAuthenticationResponse register(final SignUpRequest request) {
//    return new JwtAuthenticationResponse(jwtService.generateToken(credentialsService.save(request, passwordEncoder)));
//}

public JwtAuthenticationResponse login(final String ticket) throws AuthenticationException {
    final MephiAuthenticationToken springAuthToken = getSpringAuthToken(ticket);

    if (springAuthToken == null) throw new BadCredentialsException("CAS authentication failed");
    final MephiAuthenticationToken authenticated =
        (MephiAuthenticationToken) authenticationManager.authenticate(springAuthToken);

    final String jwtToken = jwtService.generateToken(authenticated.getDbCredentials());
    return new JwtAuthenticationResponse(jwtToken);
}

private static MephiAuthenticationToken getSpringAuthToken(final String ticket) {
    try (final HttpClient client = HttpClient.newHttpClient()) {
        final String resp = client
            .send(HttpRequest.newBuilder().uri(
                                 new URI("https://auth.mephi.ru/serviceValidate?ticket=" + ticket + "&service=https://beststudents.mephi.ru"))
                             //new URI("https://auth.mephi.ru/serviceValidate?ticket=" + ticket + "&service=100.97.110.74:5173"))
                             .timeout(java.time.Duration.ofMillis(700))
                             .GET().build(), HttpResponse.BodyHandlers.ofString())
            .body();

        final CasUserXml casResponse = XML_MAPPER.readValue(resp, CasUserXml.class);
        if (casResponse.getAuthenticationSuccess() != null &&
            casResponse.getAuthenticationSuccess().getMephiLogin() != null) {
            return new MephiAuthenticationToken(casResponse);
        } else {
            return null;
        }
    } catch (final Exception e) {
        return null;
    }
}
}