package io.github.tandemdude.notcord.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageCreateRequestBody {
    private @NotNull String content;
    private Integer nonce;
}
