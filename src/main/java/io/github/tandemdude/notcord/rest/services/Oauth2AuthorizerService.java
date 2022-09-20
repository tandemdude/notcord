package io.github.tandemdude.notcord.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.utils.JwtUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
public class Oauth2AuthorizerService {
    // TODO - conversion of string scopes to integer
    private static final Set<Scope> defaultSignedInUserScopes = Collections.emptySet();

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final Oauth2TokenPairRepository oauth2TokenPairRepository;

    public Oauth2AuthorizerService(JwtUtil jwtUtil, ObjectMapper objectMapper, Oauth2TokenPairRepository oauth2TokenPairRepository) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
    }

    public Mono<Oauth2TokenPair> generateUserTokenFromSignIn(User user) {
        // Expires in 6 months
        var accessToken = jwtUtil.generateToken(Map.of("userId", user.getId()), 15780000);
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer", accessToken, null, Instant.now().plusSeconds(15778200), 15780000, user.getId(), 0));
    }

//    public Mono<Oauth2TokenResponse> generateTokenPair(User user, Instant accessTokenExpiresAt) {
//        return Mono.empty();
//    }
}
