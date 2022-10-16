package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Oauth2AuthorizationCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2AuthorizationCodeRepository extends ReactiveCrudRepository<Oauth2AuthorizationCode, String> {
}
