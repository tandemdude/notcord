package io.github.tandemdude.notcord.authorizer.repositories;

import io.github.tandemdude.notcord.authorizer.models.db.Oauth2Credentials;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Oauth2CredentialsRepository extends ReactiveCrudRepository<Oauth2Credentials, String> {
}
