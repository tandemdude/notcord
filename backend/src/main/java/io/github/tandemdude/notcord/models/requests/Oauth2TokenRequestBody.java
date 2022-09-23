package io.github.tandemdude.notcord.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Oauth2TokenRequestBody {
    @NotNull
    @JsonProperty("grant_type")
    private String grantType;
    private String code;
    @JsonProperty("redirect_uri")
    private String redirectUri;
    @NotNull
    @JsonProperty("client_id")
    private String clientId;
    @NotNull
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("refresh_token")
    private String refreshToken;
}
