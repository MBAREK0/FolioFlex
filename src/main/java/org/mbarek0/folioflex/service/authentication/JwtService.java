package org.mbarek0.folioflex.service.authentication;

import io.jsonwebtoken.Claims;
import org.mbarek0.folioflex.model.enums.Role;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    String generateToken(String username);

    String generateToken(Map<String, Object> claims, String username);

    String generateRefreshToken(String username);

    String generateRefreshToken(Map<String, Object> claims, String username);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    boolean isTokenValid(String token, String username);

    boolean isTokenExpired(String token);

    Date extractExpiration(String token);

    Role extractRole(String username);

    boolean validateToken(String token);
}