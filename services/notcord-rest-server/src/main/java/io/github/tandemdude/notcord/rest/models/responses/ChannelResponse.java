package io.github.tandemdude.notcord.rest.models.responses;

import io.github.tandemdude.notcord.rest.models.db.Channel;
import io.github.tandemdude.notcord.rest.models.db.enums.ChannelType;
import lombok.Data;

@Data
public class ChannelResponse {
    private final String id;
    private final ChannelType type;
    private final String guildId;
    private final String name;

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
            channel.getId(),
            channel.getType(),
            channel.getGuildId(),
            channel.getName()
        );
    }
}
