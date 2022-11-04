package io.github.tandemdude.notcord.rest.models.utility;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tandemdude.notcord.commons.enums.Scope;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Oauth2TokenPair {
    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;
    private final Instant expiresAt;
    private final long scope;
    private final String userId;

    @JsonCreator
    public Oauth2TokenPair(
        @JsonProperty("tokenType") String tokenType,
        @JsonProperty("accessToken") String accessToken,
        @JsonProperty("refreshToken") String refreshToken,
        @JsonProperty("expiresIn") long expiresIn,
        @JsonProperty("scope") String scope,
        @JsonProperty("userId") String userId
    ) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = Instant.now().plusSeconds(expiresIn);
        this.scope = Scope.bitfieldFromScopes(List.of(scope.split(" ")));
        this.userId = userId;
    }
}
