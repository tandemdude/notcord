package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    Mono<Boolean> existsByUsername(String username);
    Mono<User> findByEmail(String email);
}
