package io.github.tandemdude.notcord.authorizer.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tandemdude.notcord.authorizer.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.commons.enums.Scope;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
public class Oauth2TokenResponse {
    @JsonProperty("token_type")
    private final String tokenType;
    @JsonProperty("access_token")
    private final String accessToken;
    @JsonProperty("refresh_token")
    private final String refreshToken;
    @JsonProperty("expires_in")
    private final Long expiresIn;
    private final String scope;

    public static Oauth2TokenResponse from(Oauth2TokenPair tokenPair) {
        return new Oauth2TokenResponse(
            tokenPair.getType(), tokenPair.getAccessToken(), tokenPair.getRefreshToken(),
            Duration.between(Instant.now(), tokenPair.getAccessTokenExpiresAt()).getSeconds(),
            String.join(" ", Scope.scopesFromBitfield(tokenPair.getScope()))
        );
    }
}
