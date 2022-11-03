package io.github.tandemdude.notcord.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import lombok.Data;

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
            tokenPair.getExpiresIn(), String.join(" ", Scope.scopesFromBitfield(tokenPair.getScope()))
        );
    }
}
