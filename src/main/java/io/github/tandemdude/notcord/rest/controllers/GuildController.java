package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.db.Guild;
import io.github.tandemdude.notcord.models.requests.GuildCreateRequestBody;
import io.github.tandemdude.notcord.models.responses.GuildResponse;
import io.github.tandemdude.notcord.repositories.GuildRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@RestController("/api/guilds")
public class GuildController {
    private final GuildRepository guildRepository;

    public GuildController(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
    }

    @PostMapping
    @Transactional
    public Mono<ResponseEntity<GuildResponse>> createGuild(@RequestBody GuildCreateRequestBody body, @RequestHeader("Authorization") String token) {
        return Mono.just(body)
                .map(rb -> new Guild(new BigInteger("0"), rb.getName())) // TODO - Oauth2 for guild owner
                .flatMap(guildRepository::save)
                .map(guild -> new GuildResponse(guild.getId(), guild.getOwnerId(), guild.getName()))
                .map(ResponseEntity::ok);
    }
    // delete guild
}
