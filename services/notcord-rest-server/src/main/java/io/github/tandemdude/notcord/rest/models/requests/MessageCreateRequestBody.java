package io.github.tandemdude.notcord.rest.models.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MessageCreateRequestBody {
    @NotEmpty
    private String content;
    private Integer nonce;
}
