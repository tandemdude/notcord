package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.Guild;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends ReactiveCrudRepository<Guild, String> {
}
