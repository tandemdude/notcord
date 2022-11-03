package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.exceptions.HttpExceptionFactory;
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
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GuildResponse> createGuild(
        @Valid @RequestBody GuildCreateRequestBody body, @RequestHeader("Authorization") String token
    ) {
        // TODO - add owner to the guild as a member
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER))  // Do we want bots to be able to do this?
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .map(pair -> new Guild(pair.getUserId(), body.getName()))
            .flatMap(guildRepository::save)
            .map(GuildResponse::from);
    }

    @GetMapping("/{guildId:[1-9][0-9]+}")
    public Mono<GuildResponse> getGuild(
        @PathVariable String guildId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT, Scope.GUILDS_READ))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .flatMap(pair -> guildRepository
                .findById(guildId)  // TODO - Filter to check if owner of token has permission to read this specific guild
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist"))))
            .map(GuildResponse::from);
    }

    @DeleteMapping("/{guildId:[1-9][0-9]+}")
    @Transactional
    public Mono<ResponseEntity<Void>> deleteGuild(
        @PathVariable String guildId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .flatMap(pair -> guildRepository
                .findById(guildId)
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist")))
                .filter(guild -> guild.getOwnerId().equals(pair.getUserId()))
                .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException)))
            .flatMap(guild -> guildRepository.delete(guild).thenReturn(ResponseEntity.noContent().build()));
    }

    @Transactional
    @PostMapping(value = "/{guildId:[1-9][0-9]+}/channels", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ChannelResponse> createGuildChannel(
        @Valid @RequestBody GuildChannelCreateRequestBody body,
        @PathVariable String guildId,
        @RequestHeader("Authorization") String token
    ) {
        // TODO - check user has access to the specified guild
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .then(guildRepository.existsById(guildId))
            .filter(Boolean::booleanValue)
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist")))
            .map(unused -> Channel.newGuildChannel(body.getType(), body.getName(), guildId))
            .flatMap(channelRepository::save)
            .map(ChannelResponse::from);
    }
}
