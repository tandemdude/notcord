package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GuildCreateRequestBody {
    private @NotNull String name;
}
