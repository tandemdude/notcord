package io.github.tandemdude.notcord.authorizer.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Oauth2TokenRequestBody {
    // snake_case names necessary by OAuth spec [RFC-6749]
    @NotNull
    private String grant_type;
    private String code;
    private String redirect_uri;
    @NotNull
    private String client_id;
    @NotNull
    private String client_secret;
    private String refresh_token;
}