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

package io.github.tandemdude.notcord.rest.services;

import io.github.tandemdude.notcord.commons.enums.Scope;
import io.github.tandemdude.notcord.commons.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.rest.config.EndpointProperties;
import io.github.tandemdude.notcord.rest.models.utility.Oauth2TokenPair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class ResourceAccessControlService {
    private static final WebClient webClient = WebClient.create();

    private final EndpointProperties endpointProperties;

    public ResourceAccessControlService(EndpointProperties endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public Mono<Oauth2TokenPair> validateTokenAndCheckHasAnyScopes(String token, long... scopes) {
        var parts = token.split(" ");
        if (parts.length != 2) {
            return Mono.error(HttpExceptionFactory::tokenFormatInvalidException);
        }
        var tokenType = parts[0];
        var tokenString = parts[1];

        return webClient.get()
            .uri(endpointProperties.cleanAuthorizerUrl(), builder -> builder
                .pathSegment("/oauth/token_info")
                .queryParam("token", tokenString)
                .build())
            .accept(MediaType.APPLICATION_JSON)
            .exchangeToMono(response -> response.statusCode().is2xxSuccessful()
                ? response.bodyToMono(Oauth2TokenPair.class)
                : Mono.error(HttpExceptionFactory::invalidTokenException))
            .filter(pair -> pair.getTokenType().equals(tokenType))
            .filter(pair -> pair.getExpiresAt().isAfter(Instant.now()))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::invalidTokenException))
            .filter(pair -> Scope.grantsAny(pair.getScope(), scopes))
            .switchIfEmpty(Mono.error(HttpExceptionFactory::missingRequiredPermissionsException));
    }
}
