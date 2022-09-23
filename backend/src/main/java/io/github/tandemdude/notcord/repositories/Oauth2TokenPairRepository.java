package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2TokenPairRepository extends ReactiveCrudRepository<Oauth2TokenPair, String> {
}
