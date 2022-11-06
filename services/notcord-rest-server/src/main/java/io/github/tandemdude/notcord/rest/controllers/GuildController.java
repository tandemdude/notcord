/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.commons.enums.Scope;
import io.github.tandemdude.notcord.commons.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.rest.models.db.Channel;
import io.github.tandemdude.notcord.rest.models.db.Guild;
import io.github.tandemdude.notcord.rest.models.requests.GuildChannelCreateRequestBody;
import io.github.tandemdude.notcord.rest.models.requests.GuildCreateRequestBody;
import io.github.tandemdude.notcord.rest.models.responses.ChannelResponse;
import io.github.tandemdude.notcord.rest.models.responses.GuildResponse;
import io.github.tandemdude.notcord.rest.repositories.ChannelRepository;
import io.github.tandemdude.notcord.rest.repositories.GuildRepository;
import io.github.tandemdude.notcord.rest.services.ResourceAccessControlService;
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
    private final ResourceAccessControlService resourceAccessControlService;

    public GuildController(
        GuildRepository guildRepository,
        ChannelRepository channelRepository,
        ResourceAccessControlService resourceAccessControlService
    ) {
        this.guildRepository = guildRepository;
        this.channelRepository = channelRepository;
        this.resourceAccessControlService = resourceAccessControlService;
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GuildResponse> createGuild(
        @Valid @RequestBody GuildCreateRequestBody body, @RequestHeader("Authorization") String token
    ) {
        // TODO - add owner to the guild as a member
        // TODO - check user guild limit
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER)
            .map(pair -> new Guild(pair.getUserId(), body.getName()))
            .flatMap(guildRepository::save)
            .map(GuildResponse::from);
    }

    @GetMapping("/{guildId:[1-9][0-9]+}")
    public Mono<GuildResponse> getGuild(
        @PathVariable String guildId,
        @RequestHeader("Authorization") String token
    ) {
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(
                token,
                Scope.USER,
                Scope.BOT,
                Scope.GUILDS_READ
            )
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
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER)
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
        // TODO - check user has access to the specified guild and can create channels
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER, Scope.BOT)
            .then(guildRepository.existsById(guildId))
            .filter(Boolean::booleanValue)
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A guild with ID '" + guildId + "' does not exist")))
            .map(unused -> Channel.newGuildChannel(body.getType(), body.getName(), guildId))
            .flatMap(channelRepository::save)
            .map(ChannelResponse::from);
    }
}
