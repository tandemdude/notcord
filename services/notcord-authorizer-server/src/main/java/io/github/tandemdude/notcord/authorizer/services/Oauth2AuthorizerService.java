package io.github.tandemdude.notcord.authorizer.services;

import io.github.tandemdude.notcord.authorizer.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.authorizer.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.commons.entities.User;
import io.github.tandemdude.notcord.commons.enums.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class Oauth2AuthorizerService {
    private static final Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
    private static final SecureRandom random = new SecureRandom();

    private static final int lowerLimit = 97;  // "a"
    private static final int upperLimit = 122;  // "z"
    private static final int tokenRandomPartLength = 10;

    private static final long accessTokenDefaultExpires = 60 * 60 * 24 * 30 * 2;  // 2 Months
    private static final long refreshTokenDefaultExpires = 60 * 60 * 24 * 30 * 6;  // 6 Months

    private final Oauth2TokenPairRepository oauth2TokenPairRepository;

    public Oauth2AuthorizerService(Oauth2TokenPairRepository oauth2TokenPairRepository) {
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
    }

    public String generateToken(User linkedTo) {
        var userIdPart = encoder.encode(linkedTo.getId().getBytes(StandardCharsets.UTF_8));
        var uniquePart = encoder.encode((Thread.currentThread().getName() + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
        var randomPart = encoder.encode(random.ints(lowerLimit, upperLimit + 1)
            .limit(tokenRandomPartLength)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
            .getBytes(StandardCharsets.UTF_8));
        return new String(userIdPart) + "." + new String(uniquePart) + "." + new String(randomPart);
    }

    public Mono<Oauth2TokenPair> generateUserTokenPairForFrontend(User user) {
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer",
            generateToken(user),
            generateToken(user),
            Instant.now().plusSeconds(accessTokenDefaultExpires),
            Instant.now().plusSeconds(refreshTokenDefaultExpires),
            user.getId(),
            Scope.USER,
            null
        ));
    }

    public Mono<Oauth2TokenPair> generateTokenPair(User user, long scope, long accessTokenExpires, String clientId) {
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer",
            generateToken(user),
            generateToken(user),
            Instant.now().plusSeconds(accessTokenExpires),
            Instant.now().plusSeconds(refreshTokenDefaultExpires),
            user.getId(),
            scope,
            clientId
        ));
    }
}
