package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.config.GroupDmConfig;
import io.github.tandemdude.notcord.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.models.db.DmChannelMember;
import io.github.tandemdude.notcord.models.db.Message;
import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.requests.GroupDmChannelCreateRequestBody;
import io.github.tandemdude.notcord.models.requests.MessageCreateRequestBody;
import io.github.tandemdude.notcord.models.responses.*;
import io.github.tandemdude.notcord.models.utility.ResultContainer;
import io.github.tandemdude.notcord.models.utility.TokenContainer;
import io.github.tandemdude.notcord.repositories.DmChannelMemberRepository;
import io.github.tandemdude.notcord.repositories.MessageRepository;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.rest.services.ChannelService;
import io.github.tandemdude.notcord.rest.services.Oauth2AuthorizerService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final Oauth2AuthorizerService oauth2AuthorizerService;
    private final MessageRepository messageRepository;
    private final ChannelService channelService;
    private final GroupDmConfig groupDmConfig;
    private final DmChannelMemberRepository dmChannelMemberRepository;

    public UserController(
        UserRepository userRepository,
        Oauth2AuthorizerService oauth2AuthorizerService,
        MessageRepository messageRepository,
        ChannelService channelService,
        GroupDmConfig groupDmConfig,
        DmChannelMemberRepository dmChannelMemberRepository
    ) {
        this.userRepository = userRepository;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
        this.messageRepository = messageRepository;
        this.channelService = channelService;
        this.groupDmConfig = groupDmConfig;
        this.dmChannelMemberRepository = dmChannelMemberRepository;
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

    @Transactional
    @PostMapping("/{userId:[1-9][0-9]+}/messages")
    public Mono<MessageResponse> createMessageInDm(
        @PathVariable String userId,
        @RequestBody @Valid MessageCreateRequestBody body,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            // Get the DM channel if it exists, or create a new one
            .flatMap(pair -> createDmChannel(userId, token)
                // Create the new message
                .map(channel -> new Message(channel.getId(), pair.getUserId(), null, body)))
            // Save the created message
            .flatMap(messageRepository::save)
            .map(MessageResponse::from);
    }

    @Transactional
    @PostMapping("/-/dms/{userId:[1-9][0-9]+}")
    public Mono<ChannelResponse> createDmChannel(
        @PathVariable String userId,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            // Ensure that the user we want to create a DM channel with exists
            .flatMap(pair -> userRepository
                .findById(userId)
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("A user with ID '" + userId + "' does not exist")))
                .map(unused -> pair))
            // Find the existing DM channel between the two users if it exists, or create a new one
            .flatMap(pair -> channelService
                .resolveDmChannelBetween(pair.getUserId(), userId)
                // We couldn't find an existing DM channel, so we have to create a new one
                // TODO - maybe ensure the users can DM each other - e.g. friends or mutual guilds?
                .switchIfEmpty(Mono.defer(() -> channelService.createNewDmChannelBetween(pair.getUserId(), userId))))
            .map(ChannelResponse::from);
    }

    @GetMapping("/-/group-dms")
    public Flux<ChannelResponse> fetchGroupDmChannels(@RequestHeader("Authorization") String token) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            .flatMapMany(pair -> channelService.getDmChannelsForUser(pair.getUserId(), ChannelType.GROUP_DM))
            .flatMap(channel -> dmChannelMemberRepository.findAllByChannelId(channel.getId())
                .map(DmChannelMember::getUserId)
                .collectList()
                .map(members -> GroupDmChannelResponse.from(channel, members)));
    }

    @Transactional
    @PostMapping("/-/group-dms")
    public Mono<ResponseEntity<Object>> createGroupDmChannel(
        @RequestBody @Valid GroupDmChannelCreateRequestBody body,
        @RequestHeader("Authorization") String token
    ) {
        return oauth2AuthorizerService.extractTokenPair(token)
            .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException))
            // Check if the user's own ID is present in the list of recipients
            .filter(pair -> !body.getRecipientIds().contains(pair.getUserId()))
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.badRequestException(
                "You cannot create a group DM with yourself")))
            // Check if the user has already reached the limit of joined group DM channels
            .flatMap(pair -> channelService.getDmChannelsForUser(pair.getUserId(), ChannelType.GROUP_DM)
                .collectList()
                .filter(channels -> channels.size() < groupDmConfig.getMaxChannelsPerUser())
                // Throw an exception if the user is in too many group DMs to create another
                .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.conflictException(
                    "Maximum group DM channel limit has been reached")))
                .map(unused -> pair))
            // Check if the user has specified more recipients than are allowed in a group DM
            .filter(unused -> body.getRecipientIds().size() < groupDmConfig.getMaxMembers())
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.badRequestException(
                "'recipientIds' must contain at most " + (groupDmConfig.getMaxMembers() - 1) + " users")))
            // Check whether any of the requested recipients cannot be added to a new group DM
            .flatMap(pair -> channelService
                .validateUsersCanBeAddedToGroupDmChannel(pair.getUserId(), Flux.fromIterable(body.getRecipientIds()))
                .collectList()
                // Wrap the results in a token container, so we can still retrieve the owner ID later on
                .map(results -> new TokenContainer<>(pair, results)))
            // Wrap everything in a single result which signifies if any single one of the requested
            // recipients cannot be added to a new group DM for any reason
            .flatMap(tokenContainer -> Flux
                .fromIterable(tokenContainer.getValue())
                .any(ResultContainer::isError)
                // The actual error here doesn't matter as long as it isn't null, so we use "error" as a placeholder
                .map(bool -> new ResultContainer<>(bool ? "error" : null, tokenContainer)))
            .flatMap(result -> {
                var userResults = Flux.fromIterable(result.getResult().getValue());
                if (result.isError()) {
                    // If any of the users errored then we want to return a multi-status response
                    // outlining what each problem is. We could add a check for whether there
                    // is a single element in the flux and return a normal response instead, but I think that
                    // adds too much complexity for it to be worth it
                    return userResults
                        .filter(ResultContainer::isError).map(GroupDmCreateError::fromErrorContainer).collectList()
                        .map(MultiStatusResponse::new).map(ResponseEntity.status(207)::body);
                }
                var ownerId = result.getResult().getTokenPair().getUserId();
                // All the users can be successfully added to a group DM, so we will create the new
                // channel and return a 200 response
                return userResults
                    .map(ResultContainer::getResult).collectList()
                    .flatMap(ids -> channelService.createNewGroupDmChannel(ownerId, body.getName(), ids))
                    .map(ChannelResponse::from).map(ResponseEntity::ok);
            });
    }

    @Data
    public static class GroupDmCreateError {
        private final int status;
        private final String reason;
        private final String userId;

        public static GroupDmCreateError fromErrorContainer(ResultContainer<String> resultContainer) {
            var error = resultContainer.getError();
            return new GroupDmCreateError(
                "group_dm_limit_reached".equals(error) ? 409
                    : "not_found".equals(error) ? 404
                    : "not_friends".equals(error) ? 403 : 500,
                resultContainer.getError(),
                resultContainer.getResult()
            );
        }
    }
}
