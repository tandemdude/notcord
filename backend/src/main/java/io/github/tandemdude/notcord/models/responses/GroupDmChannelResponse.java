package io.github.tandemdude.notcord.models.responses;

import io.github.tandemdude.notcord.models.db.Channel;
import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import lombok.Getter;

import java.util.List;

public class GroupDmChannelResponse extends ChannelResponse {
    @Getter
    private final List<String> memberIds;

    public GroupDmChannelResponse(String id, String name, List<String> memberIds) {
        super(id, ChannelType.GROUP_DM, null, name);
        this.memberIds = memberIds;
    }

    public static GroupDmChannelResponse from(Channel channel, List<String> memberIds) {
        return new GroupDmChannelResponse(
            channel.getId(), channel.getName(), memberIds
        );
    }
}
