package io.github.tandemdude.notcord.rest.services;

import io.github.tandemdude.notcord.exceptions.auth.MissingRequiredPermissionException;
import io.github.tandemdude.notcord.exceptions.auth.TokenDoesNotExistException;
import io.github.tandemdude.notcord.exceptions.auth.TokenExpiredException;
import io.github.tandemdude.notcord.exceptions.auth.TokenFormatInvalidException;
import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.responses.DefaultErrorResponse;
import io.github.tandemdude.notcord.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
public class Oauth2AuthorizerService {
    private final JwtUtil jwtUtil;
    private final Oauth2TokenPairRepository oauth2TokenPairRepository;

    public Oauth2AuthorizerService(JwtUtil jwtUtil, Oauth2TokenPairRepository oauth2TokenPairRepository) {
        this.jwtUtil = jwtUtil;
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
    }

    public String generateRefreshToken(String accessToken) {
        // Expires in 6 months
        return jwtUtil.generateToken(Map.of("for", accessToken, "type", "refresh"), 15768000);
    }

    public Mono<Oauth2TokenPair> generateUserTokenPairForFrontend(User user) {
        // Expires in 2 months
        var accessToken = jwtUtil.generateToken(Map.of("userId", user.getId(), "type", "access", "scope", Scope.USER), 5260000);
        var refreshToken = generateRefreshToken(accessToken);
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer", accessToken, refreshToken, Instant.now().plusSeconds(5260000), 5258200, user.getId(), Scope.USER, null));
    }

    public Mono<Oauth2TokenPair> generateTokenPair(User user, long scope, long accessTokenLifetime, String clientId) {
        var accessToken = jwtUtil.generateToken(Map.of("userId", user.getId(), "type", "access", "scope", scope), accessTokenLifetime);
        var refreshToken = generateRefreshToken(accessToken);
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer", accessToken, refreshToken, Instant.now().plusSeconds(accessTokenLifetime), accessTokenLifetime, user.getId(), scope, clientId));
    }

    public Mono<Oauth2TokenPair> extractTokenPair(String token) {
        var parts = token.split(" ");
        if (parts.length != 2) {
            return Mono.error(TokenFormatInvalidException::new);
        }
        var tokenType = parts[0];
        var tokenString = parts[1];

        return oauth2TokenPairRepository.findByAccessToken(tokenString)
            .switchIfEmpty(Mono.error(TokenDoesNotExistException::new))
            .filter(pair -> pair.getType().equals(tokenType))
            .switchIfEmpty(Mono.error(TokenDoesNotExistException::new))
            .filter(pair -> Instant.now().isBefore(pair.getExpiresAt()))
            .switchIfEmpty(Mono.error(TokenExpiredException::new));
    }

    public Mono<ResponseEntity<Object>> handleCommonAuthorizationErrors(Mono<ResponseEntity<Object>> mono) {
        return mono
            .onErrorReturn(MissingRequiredPermissionException.class, ResponseEntity.status(403)
                .body(new DefaultErrorResponse("missing_permissions", "You do not have permission to access the requested resource")))
            .onErrorReturn(TokenFormatInvalidException.class, ResponseEntity.status(401)
                .body(new DefaultErrorResponse("invalid_token", "Token format is invalid - must match: '<type> <token>'")))
            .onErrorReturn(TokenDoesNotExistException.class, ResponseEntity.status(401)
                .body(new DefaultErrorResponse("invalid_token", "The supplied token is not recognised")))
            .onErrorReturn(TokenExpiredException.class, ResponseEntity.status(401)
                .body(new DefaultErrorResponse("invalid_token", "The supplied token is not recognised")));
    }
}
