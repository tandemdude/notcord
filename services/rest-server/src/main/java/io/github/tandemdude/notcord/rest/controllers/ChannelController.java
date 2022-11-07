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
import io.github.tandemdude.notcord.rest.models.db.DmChannelMember;
import io.github.tandemdude.notcord.rest.models.db.Message;
import io.github.tandemdude.notcord.rest.models.requests.MessageCreateRequestBody;
import io.github.tandemdude.notcord.rest.models.responses.ChannelResponse;
import io.github.tandemdude.notcord.rest.models.responses.GroupDmChannelResponse;
import io.github.tandemdude.notcord.rest.models.responses.MessageResponse;
import io.github.tandemdude.notcord.rest.repositories.ChannelRepository;
import io.github.tandemdude.notcord.rest.repositories.DmChannelMemberRepository;
import io.github.tandemdude.notcord.rest.repositories.MessageRepository;
import io.github.tandemdude.notcord.rest.services.ResourceAccessControlService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelRepository channelRepository;
    private final ResourceAccessControlService resourceAccessControlService;
    private final DmChannelMemberRepository dmChannelMemberRepository;
    private final MessageRepository messageRepository;

    public ChannelController(
        ChannelRepository channelRepository,
        ResourceAccessControlService resourceAccessControlService,
        DmChannelMemberRepository dmChannelMemberRepository,
        MessageRepository messageRepository
    ) {
        this.channelRepository = channelRepository;
        this.resourceAccessControlService = resourceAccessControlService;
        this.dmChannelMemberRepository = dmChannelMemberRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{channelId:[1-9][0-9]+}")
    public Mono<ChannelResponse> getChannel(
        @PathVariable String channelId,
        @RequestHeader("Authorization") String token
    ) {
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER, Scope.BOT)
            .flatMap(tokenInfo -> channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A channel with ID '" + channelId + "' does not exist")))
                // Check if the found channel is a DM channel, and if it is, check that the user has access to it
                .filterWhen(channel -> dmChannelMemberRepository
                    .existsByChannelIdAndUserId(channelId, tokenInfo.getUserId())
                    .map(cond1 -> cond1 || !(channel.isDm() || channel.isGroupDm())))
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A channel with ID '" + channelId + "' does not exist"))))
            // TODO - If the channel is in a guild, check that the user is in that guild
            .flatMap(channel -> !channel.isGroupDm() ? Mono.just(ChannelResponse.from(channel))
                : dmChannelMemberRepository.findAllByChannelId(channelId)
                .map(DmChannelMember::getUserId)
                .collectList()
                .map(members -> GroupDmChannelResponse.from(channel, members)));
    }

    @GetMapping("/{channelId:[1-9][0-9]+}/messages/{messageId:[1-9][0-9]+}")
    public Mono<MessageResponse> getMessage(
        @PathVariable String channelId,
        @PathVariable String messageId,
        @RequestHeader("Authorization") String token
    ) {
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER, Scope.BOT)
            // TODO - check channel exists and that user has permission to read messages in the channel
            .flatMap(tokenInfo -> messageRepository.findByIdAndChannelId(messageId, channelId))
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A message with ID '" + messageId + "' does not exist")))
            .map(MessageResponse::from);
    }

    @Transactional
    @PostMapping(value = "/{channelId:[1-9][0-9]+}/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<MessageResponse> createMessage(
        @PathVariable String channelId,
        @RequestBody @Valid MessageCreateRequestBody body,
        @RequestHeader("Authorization") String token
    ) {
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER, Scope.BOT)
            .flatMap(tokenInfo -> getChannel(channelId, token)
                // TODO - If channel in guild, check user has permission to send a message
                // .filterWhen(/* ... */).switchIfEmpty(/* ... */)
                .map(channel -> new Message(channelId, tokenInfo.getUserId(), channel.getGuildId(), body)))
            .flatMap(messageRepository::save)
            .map(MessageResponse::from);
    }

    @Transactional
    @DeleteMapping("/{channelId:[1-9][0-9]+}/messages/{messageId:[1-9][0-9]+}")
    public Mono<ResponseEntity<Void>> deleteMessage(
        @PathVariable String channelId,
        @PathVariable String messageId,
        @RequestHeader("Authorization") String token
    ) {
        return resourceAccessControlService.validateTokenAndCheckHasAnyScopes(token, Scope.USER, Scope.BOT)
            .flatMap(tokenInfo -> messageRepository
                .findByIdAndChannelId(messageId, channelId)
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A message with ID '" + messageId + "' does not exist in channel '" + channelId + "'")))
                // TODO - if in guild, check user has permissions to delete others' messages
                .filter(message -> message.getAuthorId().equals(tokenInfo.getUserId()))
                .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException)))
            .flatMap(messageRepository::delete)
            .thenReturn(ResponseEntity.noContent().build());
    }
}
