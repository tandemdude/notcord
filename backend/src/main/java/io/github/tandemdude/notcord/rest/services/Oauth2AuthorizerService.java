package io.github.tandemdude.notcord.rest.services;

import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.utils.JwtUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Service
public class Oauth2AuthorizerService {
    // TODO - conversion of string scopes to integer
    private final JwtUtil jwtUtil;
    private final Oauth2TokenPairRepository oauth2TokenPairRepository;

    public Oauth2AuthorizerService(JwtUtil jwtUtil, Oauth2TokenPairRepository oauth2TokenPairRepository) {
        this.jwtUtil = jwtUtil;
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
    }

    public String generateRefreshToken(String accessToken) {
        // Expires in 1 year
        return jwtUtil.generateToken(Map.of("for", accessToken), 31536000);
    }

    public Mono<Oauth2TokenPair> generateUserTokenPairFromSignIn(User user) {
        // Expires in 2 months
        var accessToken = jwtUtil.generateToken(Map.of("userId", user.getId()), 5260000);
        var refreshToken = generateRefreshToken(accessToken);
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer", accessToken, refreshToken, Instant.now().plusSeconds(5260000), 5258200, user.getId(), Scope.USER));
    }

//    public Mono<Oauth2TokenResponse> generateTokenPair(User user, Instant accessTokenExpiresAt) {
//        return Mono.empty();
//    }
}
