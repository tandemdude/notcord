package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Oauth2TokenPairRepository extends ReactiveCrudRepository<Oauth2TokenPair, String> {
    Mono<Oauth2TokenPair> findByRefreshToken(String refreshToken);
}
