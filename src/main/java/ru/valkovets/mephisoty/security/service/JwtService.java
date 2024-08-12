package ru.valkovets.mephisoty.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.valkovets.mephisoty.db.model.userdata.Credentials;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
@Value("${token.signing.key}")
private String jwtSigningKey = "default";

/**
 * Извлечение имени пользователя из токена
 *
 * @param token токен
 * @return имя пользователя
 */
public String extractUserName(final String token) {
    return extractClaim(token, Claims::getSubject);
}

/**
 * Генерация токена
 *
 * @param userDetails данные пользователя
 * @return токен
 */
public String generateToken(final UserDetails userDetails) {
    final Map<String, Object> claims = new HashMap<>();
    if (userDetails instanceof final Credentials customUserDetails) {
        claims.put("id", customUserDetails.getId());
        claims.put("email", customUserDetails.getEmail());
        claims.put("role", customUserDetails.getRole());
    }
    return generateToken(claims, userDetails);
}

/**
 * Проверка токена на валидность
 *
 * @param token       токен
 * @param userDetails данные пользователя
 * @return true, если токен валиден
 */
public boolean isTokenValid(final String token, final UserDetails userDetails) {
    final String userName = extractUserName(token);
    return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

/**
 * Извлечение данных из токена
 *
 * @param token           токен
 * @param claimsResolvers функция извлечения данных
 * @param <T>             тип данных
 * @return данные
 */
private <T> T extractClaim(final String token, final Function<? super Claims, T> claimsResolvers) {
    final Claims claims = extractAllClaims(token);
    return claimsResolvers.apply(claims);
}

/**
 * Генерация токена
 *
 * @param extraClaims дополнительные данные
 * @param userDetails данные пользователя
 * @return токен
 */
private String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
               .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
}

/**
 * Проверка токена на просроченность
 *
 * @param token токен
 * @return true, если токен просрочен
 */
private boolean isTokenExpired(final String token) {
    return extractExpiration(token).before(new Date());
}

/**
 * Извлечение даты истечения токена
 *
 * @param token токен
 * @return дата истечения
 */
private Date extractExpiration(final String token) {
    return extractClaim(token, Claims::getExpiration);
}

/**
 * Извлечение всех данных из токена
 *
 * @param token токен
 * @return данные
 */
private Claims extractAllClaims(final String token) {
    return Jwts.parser().setSigningKey(getSigningKey()).build()
               .parseClaimsJws(token).getBody();
}

/**
 * Получение ключа для подписи токена
 *
 * @return ключ
 */
private Key getSigningKey() {
    final byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
    return Keys.hmacShaKeyFor(keyBytes);
}
}
