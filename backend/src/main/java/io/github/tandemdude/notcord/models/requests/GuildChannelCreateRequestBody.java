package io.github.tandemdude.notcord.models.requests;

import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuildChannelCreateRequestBody {
    private @NotNull String name;
    private @NotNull ChannelType type;
}
