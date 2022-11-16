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

package io.github.tandemdude.notcord.commons.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tandemdude.notcord.commons.enums.Scope;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Oauth2TokenInfo {
    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;
    private final Instant expiresAt;
    private final long scope;
    private final String userId;

    @JsonCreator
    public Oauth2TokenInfo(
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
