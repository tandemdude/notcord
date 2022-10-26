package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.models.db.DmChannelMember;
import io.github.tandemdude.notcord.models.db.Message;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.requests.MessageCreateRequestBody;
import io.github.tandemdude.notcord.models.responses.ChannelResponse;
import io.github.tandemdude.notcord.models.responses.GroupDmChannelResponse;
import io.github.tandemdude.notcord.models.responses.MessageResponse;
import io.github.tandemdude.notcord.repositories.ChannelRepository;
import io.github.tandemdude.notcord.repositories.DmChannelMemberRepository;
import io.github.tandemdude.notcord.repositories.MessageRepository;
import io.github.tandemdude.notcord.rest.services.Oauth2AuthorizerService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelRepository channelRepository;
    private final Oauth2AuthorizerService oauth2AuthorizerService;
    private final DmChannelMemberRepository dmChannelMemberRepository;
    private final MessageRepository messageRepository;

    public ChannelController(
        ChannelRepository channelRepository,
        Oauth2AuthorizerService oauth2AuthorizerService,
        DmChannelMemberRepository dmChannelMemberRepository,
        MessageRepository messageRepository
    ) {
        this.channelRepository = channelRepository;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
        this.dmChannelMemberRepository = dmChannelMemberRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{channelId:[1-9][0-9]+}")
    public Mono<ChannelResponse> getChannel(
        @PathVariable String channelId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .flatMap(pair -> channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A channel with ID '" + channelId + "' does not exist")))
                // Check if the found channel is a DM channel, and if it is, check that the user has access to it
                .filterWhen(channel -> dmChannelMemberRepository
                    .existsByChannelIdAndUserId(channelId, pair.getUserId())
                    .map(cond1 -> cond1 || !(channel.isDm() || channel.isGroupDm())))
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A channel with ID '" + channelId + "' does not exist"))))
            // TODO - If the channel is in a guild, check that the user is in that guild
            .flatMap(channel -> !channel.isGroupDm() ? Mono.just(ChannelResponse.from(channel))
                : dmChannelMemberRepository.findAllByChannelId(channelId)
                .map(DmChannelMember::getUserId)
                .collectList()
                .map(members -> GroupDmChannelResponse.from(channel, members)));
    }

    @PostMapping("/{channelId:[1-9][0-9]+}/messages")
    public Mono<MessageResponse> createMessageInChannel(
        @PathVariable String channelId,
        @RequestBody @Valid MessageCreateRequestBody body,
        @RequestHeader("Authorization") String token
    ) {

        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .flatMap(pair -> getChannel(channelId, token)
                // TODO - If channel in guild, check user has permission to send a message
                // .filterWhen(/* ... */).switchIfEmpty(/* ... */)
                .map(channel -> new Message(channelId, pair.getUserId(), channel.getGuildId(), body)))
            .flatMap(messageRepository::save)
            .map(MessageResponse::from);
    }
}
