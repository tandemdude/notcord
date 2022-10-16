package io.github.tandemdude.notcord.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthorizationErrorResponse {
    private final String error;
    @JsonProperty("error_description")
    private final String errorDescription;
}
