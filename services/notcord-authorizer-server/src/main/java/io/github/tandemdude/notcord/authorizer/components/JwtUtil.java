package io.github.tandemdude.notcord.authorizer.components;

import io.github.tandemdude.notcord.authorizer.config.JwtConfig;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final JwtConfig jwtConfig;

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(Map<String, Object> claims, long expiresInSeconds) {
        var newClaims = new HashMap<>(claims);
        return Jwts.builder()
            .setClaims(newClaims)
            .setExpiration(new Date(Instant.now().toEpochMilli() + (expiresInSeconds * 1000)))
            .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())
            .compact();
    }

    public Optional<Map<String, Object>> parseToken(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException ex) {
            logger.debug("Error occurred while decoding JWT token", ex);
        }
        return Optional.empty();
    }

    public Optional<Map<String, Object>> parseTokenAllowExpired(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token).getBody());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            logger.debug("Error occurred while decoding JWT token", ex);
        } catch (ExpiredJwtException ex) {
            return Optional.of(ex.getClaims());
        }
        return Optional.empty();
    }
}
