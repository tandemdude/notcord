package io.github.tandemdude.notcord.rest.services;

import io.github.tandemdude.notcord.rest.config.GroupDmConfig;
import io.github.tandemdude.notcord.rest.models.db.Channel;
import io.github.tandemdude.notcord.rest.models.db.DmChannelMember;
import io.github.tandemdude.notcord.rest.models.db.enums.ChannelType;
import io.github.tandemdude.notcord.rest.models.utility.ResultContainer;
import io.github.tandemdude.notcord.rest.repositories.ChannelRepository;
import io.github.tandemdude.notcord.rest.repositories.DmChannelMemberRepository;
import io.github.tandemdude.notcord.rest.repositories.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChannelService {
    private final DmChannelMemberRepository dmChannelMemberRepository;
    private final ChannelRepository channelRepository;
    private final GroupDmConfig groupDmConfig;
    private final UserRepository userRepository;

    public ChannelService(
        DmChannelMemberRepository dmChannelMemberRepository,
        ChannelRepository channelRepository,
        GroupDmConfig groupDmConfig,
        UserRepository userRepository
    ) {
        this.dmChannelMemberRepository = dmChannelMemberRepository;
        this.channelRepository = channelRepository;
        this.groupDmConfig = groupDmConfig;
        this.userRepository = userRepository;
    }

    public Mono<Channel> resolveDmChannelBetween(String userId1, String userId2) {
        return dmChannelMemberRepository
            // Find all the open dm channels for the user which was authorized by the token
            .findAllByUserIdAndChannelType(userId1, ChannelType.DM)
            .map(DmChannelMember::getChannelId)
            .collect(Collectors.toSet())
            .flatMap(set1 -> dmChannelMemberRepository
                // Find all the open dm channels for the user ID provided in the path variable
                .findAllByUserIdAndChannelType(userId2, ChannelType.DM)
                .map(DmChannelMember::getChannelId)
                .collect(Collectors.toSet())
                // Calculate the intersection between both sets of dm channels for each user
                .doOnNext(set2 -> set2.retainAll(set1)))
            // Find the first member in the set intersection - there should be 2 or 0 items to choose from
            .map(intersection -> intersection.stream().findFirst())
            // If there were matching members then extract one of them or return an empty Mono
            .flatMap(maybeChannelId -> maybeChannelId.map(Mono::just).orElseGet(Mono::empty))
            .flatMap(channelRepository::findById);
    }

    public Flux<Channel> getDmChannelsForUser(String userId, ChannelType type) {
        return dmChannelMemberRepository.findAllByUserIdAndChannelType(userId, type)
            .flatMap(member -> channelRepository.findById(member.getChannelId()));
    }

    public Mono<Channel> createNewDmChannelBetween(String userId1, String userId2) {
        return channelRepository.save(Channel.newDmChannel())
            .flatMap(channel -> dmChannelMemberRepository
                .saveAll(List.of(
                    new DmChannelMember(ChannelType.DM, channel.getId(), userId1),
                    new DmChannelMember(ChannelType.DM, channel.getId(), userId2)
                ))
                .collectList()
                .map(unused -> channel));
    }

    public Mono<Channel> createNewGroupDmChannel(String ownerId, String name, List<String> recipientIds) {
        return channelRepository.save(Channel.newGroupDmChannel(name, groupDmConfig.getMaxMembers(), ownerId))
            .flatMap(channel -> dmChannelMemberRepository
                // Ensure to link the owner of the DM channel to it as well as the other recipients
                .saveAll(Stream.concat(recipientIds.stream(), Stream.of(ownerId))
                    .map(userId -> new DmChannelMember(ChannelType.GROUP_DM, channel.getId(), userId))
                    .toList())
                .collectList()
                .map(unused -> channel));
    }

    public Flux<ResultContainer<String>> validateUsersCanBeAddedToGroupDmChannel(String ownerId, Flux<String> userIds) {
        return userIds
            // Check first whether all the requested users actually exist
            .flatMap(id -> userRepository
                .findById(id)
                // The user exists
                .map(user -> new ResultContainer<>(null, user.getId()))
                // The user does not exist
                .defaultIfEmpty(new ResultContainer<>("not_found", id)))
            // TODO - check here that the user has permissions to add all the requested users to the DM
            // Check if any of the requested users have already reached the max number of group DMs
            // Return the previous result if it contained an error - we don't need to check this condition
            .flatMap(result -> result.isError() ? Mono.just(result)
                : getDmChannelsForUser(result.getResult(), ChannelType.GROUP_DM)
                .collectList()
                .map(channels -> channels.size() < groupDmConfig.getMaxChannelsPerUser()
                    ? result : new ResultContainer<>("group_dm_limit_reached", result.getResult())));
    }
}
