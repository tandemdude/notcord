package io.github.tandemdude.notcord.models.responses;

import io.github.tandemdude.notcord.models.db.Channel;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ChannelResponse {
    private final String id;
    private final int type;
    private final @Nullable String guildId;
    private final String name;

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(channel.getId(),
            channel.getType().getValue(),
            channel.getGuildId(),
            channel.getName()
        );
    }
}
