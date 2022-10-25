package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, String> {
}
