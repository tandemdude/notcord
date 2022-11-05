/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
