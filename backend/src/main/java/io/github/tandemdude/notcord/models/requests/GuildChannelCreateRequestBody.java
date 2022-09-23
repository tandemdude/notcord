package io.github.tandemdude.notcord.models.requests;

import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GuildChannelCreateRequestBody {
    private @NotNull String name;
    private @NotNull ChannelType type;
}
