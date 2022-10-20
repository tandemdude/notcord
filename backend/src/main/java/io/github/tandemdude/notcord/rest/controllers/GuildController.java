package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.exceptions.ExceptionFactory;
import io.github.tandemdude.notcord.models.db.Channel;
import io.github.tandemdude.notcord.models.db.Guild;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.requests.GuildChannelCreateRequestBody;
import io.github.tandemdude.notcord.models.requests.GuildCreateRequestBody;
import io.github.tandemdude.notcord.models.responses.ChannelResponse;
import io.github.tandemdude.notcord.models.responses.GuildResponse;
import io.github.tandemdude.notcord.repositories.ChannelRepository;
import io.github.tandemdude.notcord.repositories.GuildRepository;
import io.github.tandemdude.notcord.rest.services.Oauth2AuthorizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/guilds")
public class GuildController {
    private final GuildRepository guildRepository;
    private final ChannelRepository channelRepository;
    private final Oauth2AuthorizerService oauth2AuthorizerService;

    public GuildController(
        GuildRepository guildRepository,
        ChannelRepository channelRepository,
        Oauth2AuthorizerService oauth2AuthorizerService
    ) {
        this.guildRepository = guildRepository;
        this.channelRepository = channelRepository;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
    }

    @PostMapping
    @Transactional
    public Mono<ResponseEntity<Object>> createGuild(
        @Valid @RequestBody GuildCreateRequestBody body, @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER))  // Do we want bots to be able to do this?
            .switchIfEmpty(Mono.error(ExceptionFactory::missingRequiredPermissionsException))
            .map(pair -> new Guild(pair.getUserId(), body.getName()))
            .flatMap(guildRepository::save)
            .map(GuildResponse::from)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/{guildId:[1-9][0-9]+}")
    public Mono<ResponseEntity<Object>> getGuild(
        @PathVariable String guildId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT, Scope.GUILDS_READ))
            .switchIfEmpty(Mono.error(ExceptionFactory::missingRequiredPermissionsException))
            .flatMap(pair -> guildRepository
                .findById(guildId)  // TODO - Filter to check if owner of token has permission to read this specific guild
                .switchIfEmpty(Mono.error(() -> ExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist"))))
            .map(GuildResponse::from)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{guildId:[1-9][0-9]+}")
    @Transactional
    public Mono<ResponseEntity<Object>> deleteGuild(
        @PathVariable String guildId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER))
            .switchIfEmpty(Mono.error(ExceptionFactory::missingRequiredPermissionsException))
            .flatMap(pair -> guildRepository
                .findById(guildId)
                .switchIfEmpty(Mono.error(() -> ExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist")))
                .filter(guild -> guild.getOwnerId().equals(pair.getUserId()))
                .switchIfEmpty(Mono.error(ExceptionFactory::missingRequiredPermissionsException)))
            .flatMap(guild -> guildRepository.delete(guild).thenReturn(ResponseEntity.noContent().build()));
    }

    @PostMapping("/{guildId:[1-9][0-9]+}/channels")
    @Transactional
    public Mono<ResponseEntity<ChannelResponse>> createGuildChannel(
        @Valid @RequestBody GuildChannelCreateRequestBody body, @PathVariable String guildId
    ) {
        // TODO - authorization
        return guildRepository.existsById(guildId)
            .flatMap(exists -> exists ? Mono.just(body) : Mono.empty())
            .switchIfEmpty(Mono.error(() -> ExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist")))
            .map(rb -> new Channel(rb.getType(), guildId, rb.getName()))
            .flatMap(channelRepository::save)
            .map(ChannelResponse::from)
            .map(ResponseEntity::ok);
    }
}
