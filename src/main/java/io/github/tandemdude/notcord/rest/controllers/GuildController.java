package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.db.Channel;
import io.github.tandemdude.notcord.models.db.Guild;
import io.github.tandemdude.notcord.models.requests.GuildChannelCreateRequestBody;
import io.github.tandemdude.notcord.models.requests.GuildCreateRequestBody;
import io.github.tandemdude.notcord.models.responses.ChannelResponse;
import io.github.tandemdude.notcord.models.responses.GuildResponse;
import io.github.tandemdude.notcord.repositories.ChannelRepository;
import io.github.tandemdude.notcord.repositories.GuildRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/guilds")
public class GuildController {
    private final GuildRepository guildRepository;
    private final ChannelRepository channelRepository;

    public GuildController(GuildRepository guildRepository, ChannelRepository channelRepository) {
        this.guildRepository = guildRepository;
        this.channelRepository = channelRepository;
    }

    @PostMapping
    @Transactional
    public Mono<ResponseEntity<GuildResponse>> createGuild(
        @Valid @RequestBody GuildCreateRequestBody body, @RequestHeader("Authorization") String token
    ) {
        // TODO - Oauth2 for guild owner
        return Mono.just(body)
            .map(rb -> new Guild(new BigInteger("0"), rb.getName()))
            .flatMap(guildRepository::save)
            .map(GuildResponse::from)
            .map(ResponseEntity::ok);
    }
    // delete guild

    @PostMapping("/{guildId:[1-9][0-9]+}/channels")
    @Transactional
    public Mono<ResponseEntity<ChannelResponse>> createGuildChannel(
        @Valid @RequestBody GuildChannelCreateRequestBody body, @PathVariable String guildId
    ) {
        return guildRepository.existsById(guildId)
            .flatMap(exists -> exists ? Mono.just(body) : Mono.empty())
            .map(rb -> new Channel(rb.getType(), guildId, rb.getName()))
            .flatMap(channelRepository::save)
            .map(ChannelResponse::from)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
