package io.github.tandemdude.notcord.authorizer.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OauthErrorResponse {
    private final String error;
    @JsonProperty("error_description")
    private final String errorDescription;
}
