package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.models.db.Channel;
import io.github.tandemdude.notcord.models.db.DmChannelMember;
import io.github.tandemdude.notcord.models.db.Message;
import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.responses.UserResponse;
import io.github.tandemdude.notcord.repositories.ChannelRepository;
import io.github.tandemdude.notcord.repositories.DmChannelMemberRepository;
import io.github.tandemdude.notcord.repositories.MessageRepository;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.rest.services.Oauth2AuthorizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final Oauth2AuthorizerService oauth2AuthorizerService;
    private final DmChannelMemberRepository dmChannelMemberRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public UserController(
        UserRepository userRepository,
        Oauth2AuthorizerService oauth2AuthorizerService,
        DmChannelMemberRepository dmChannelMemberRepository,
        ChannelRepository channelRepository,
        MessageRepository messageRepository
    ) {
        this.userRepository = userRepository;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
        this.dmChannelMemberRepository = dmChannelMemberRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{userId:[1-9][0-9]+}")
    public Mono<ResponseEntity<Object>> getUser(
        @PathVariable String userId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .flatMap(unused -> userRepository.findById(userId))
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A user with ID '" + userId + "' does not exist")))
            .map(UserResponse::from)
            .map(ResponseEntity::ok);
    }

    @PostMapping("/{userId:[1-9][0-9]+}/messages")
    public Mono<?> createMessageInDm(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        // TODO - request body
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            // Ensure that the user we want to send a DM to exists
            .flatMap(pair -> userRepository
                .findById(userId)
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A user with ID '" + userId + "' does not exist")))
                .map(unused -> pair))
            // Find the existing DM channel between the two users if it exists, or create a new one
            .flatMap(pair -> dmChannelMemberRepository
                // Find all the open dm channels for the user which was authorized by the token
                .findAllByUserIdAndChannelType(pair.getUserId(), ChannelType.DM)
                .map(DmChannelMember::getChannelId)
                .collect(Collectors.toSet())
                .flatMap(set1 -> dmChannelMemberRepository
                    // Find all the open dm channels for the user ID provided in the path variable
                    .findAllByUserIdAndChannelType(userId, ChannelType.DM)
                    .map(DmChannelMember::getChannelId)
                    .collect(Collectors.toSet())
                    // Calculate the intersection between both sets of dm channels for each user
                    .doOnNext(set2 -> set2.retainAll(set1)))
                // Find the first member in the set intersection - there should be 2 or 0 items to choose from
                .map(intersection -> intersection.stream().findFirst())
                // If there were matching members then extract one of them or return an empty Mono
                .flatMap(maybeMember -> maybeMember.map(Mono::just).orElseGet(Mono::empty))
                // We couldn't find two records that match, so we have to create a new channel
                // TODO - maybe ensure the users can DM each other - e.g. friends or mutual guilds?
                .switchIfEmpty(Mono.defer(() -> channelRepository.save(Channel.newDmChannel()))
                    // Bind the path param user to the new DM channel
                    .flatMap(channel -> dmChannelMemberRepository.save(new DmChannelMember(
                        ChannelType.DM,
                        channel.getId(),
                        userId
                    )))
                    // Bind the authorized user to the new DM channel
                    .flatMap(member -> dmChannelMemberRepository.save(new DmChannelMember(
                        ChannelType.DM,
                        member.getChannelId(),
                        pair.getUserId()
                    )))
                    .map(DmChannelMember::getChannelId))
                // Create the new message
                // We do it here to ensure we still have access to the author's ID from the token pair
                .map(channelId -> new Message(channelId, pair.getUserId(), null, "amongus")))
            // Save the created message
            .flatMap(messageRepository::save);
    }
}
