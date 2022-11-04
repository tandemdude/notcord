package io.github.tandemdude.notcord.rest.models.responses;

import io.github.tandemdude.notcord.rest.models.db.Message;
import lombok.Data;

@Data
public class MessageResponse {
    private final String id;
    private final String channelId;
    private final String authorId;
    private final String guildId;
    private final String content;
    private final Integer nonce;

    public static MessageResponse from(Message message) {
        return new MessageResponse(
            message.getId(),
            message.getChannelId(),
            message.getAuthorId(),
            message.getGuildId(),
            message.getContent(),
            message.getNonce()
        );
    }
}
