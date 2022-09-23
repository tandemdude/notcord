package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Channel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends ReactiveCrudRepository<Channel, String> {
}
