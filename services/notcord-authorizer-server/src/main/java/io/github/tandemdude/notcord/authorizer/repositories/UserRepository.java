package io.github.tandemdude.notcord.authorizer.repositories;

import io.github.tandemdude.notcord.commons.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<Boolean> existsByUsername(String username);

    Mono<User> findByEmail(String email);
}
