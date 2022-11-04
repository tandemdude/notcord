package io.github.tandemdude.notcord.rest.repositories;

import io.github.tandemdude.notcord.rest.models.db.Guild;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildRepository extends ReactiveCrudRepository<Guild, String> {
}
