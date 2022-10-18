package io.github.tandemdude.notcord.models.responses;

import io.github.tandemdude.notcord.models.db.Guild;
import lombok.Data;

@Data
public class GuildResponse {
    private final String id;
    private final String ownerId;
    private final String name;

    public static GuildResponse from(Guild guild) {
        return new GuildResponse(guild.getId(), guild.getOwnerId(), guild.getName());
    }
}
