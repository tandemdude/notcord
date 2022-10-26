package io.github.tandemdude.notcord.models.requests;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessageCreateRequestBody {
    private @NotNull String content;
    private Integer nonce;
}
