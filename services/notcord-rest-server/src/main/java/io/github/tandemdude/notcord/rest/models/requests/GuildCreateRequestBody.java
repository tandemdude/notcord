package io.github.tandemdude.notcord.rest.models.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GuildCreateRequestBody {
    @NotEmpty
    private String name;
}
