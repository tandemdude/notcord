package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Oauth2AuthorizationCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface Oauth2AuthorizationCodeRepository extends ReactiveCrudRepository<Oauth2AuthorizationCode, String> {
    Mono<Oauth2AuthorizationCode> findByCodeAndClientId(String code, String clientId);
}
