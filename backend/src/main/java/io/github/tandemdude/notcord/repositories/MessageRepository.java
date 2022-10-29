package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, String> {
    Mono<Message> findByIdAndChannelId(String id, String channelId);
}
