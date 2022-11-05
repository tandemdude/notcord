package io.github.tandemdude.notcord.authorizer.models.responses;

import io.github.tandemdude.notcord.authorizer.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.commons.enums.Scope;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
public class Oauth2TokenInfoResponse {
    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;
    private final Long expiresIn;
    private final String scope;
    private final String userId;

    public static Oauth2TokenInfoResponse from(Oauth2TokenPair tokenPair) {
        return new Oauth2TokenInfoResponse(
            tokenPair.getType(), tokenPair.getAccessToken(), tokenPair.getRefreshToken(),
            Duration.between(Instant.now(), tokenPair.getAccessTokenExpiresAt()).getSeconds(),
            String.join(" ", Scope.scopesFromBitfield(tokenPair.getScope())), tokenPair.getUserId()
        );
    }
}
