package io.github.tandemdude.notcord.models.responses;

import lombok.Data;

@Data
public class GuildResponse {
    private final String id;
    private final String ownerId;
    private final String name;
}
