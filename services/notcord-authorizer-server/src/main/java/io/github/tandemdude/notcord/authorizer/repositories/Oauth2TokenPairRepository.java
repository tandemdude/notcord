package io.github.tandemdude.notcord.authorizer.repositories;

import io.github.tandemdude.notcord.authorizer.models.db.Oauth2TokenPair;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Oauth2TokenPairRepository extends ReactiveCrudRepository<Oauth2TokenPair, String> {
    Mono<Oauth2TokenPair> findByRefreshToken(String refreshToken);

    Mono<Oauth2TokenPair> findByAccessToken(String accessToken);
}
