package io.github.tandemdude.notcord.repositories;

import io.github.tandemdude.notcord.models.db.DmChannelMember;
import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DmChannelMemberRepository extends ReactiveCrudRepository<DmChannelMember, String> {
    Flux<DmChannelMember> findAllByUserIdAndChannelType(String userId, ChannelType channelType);
}
