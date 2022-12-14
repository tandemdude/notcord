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

package io.github.tandemdude.notcord.authorizer.services;

import io.github.tandemdude.notcord.authorizer.models.db.Oauth2TokenPair;
import io.github.tandemdude.notcord.authorizer.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.commons.entities.User;
import io.github.tandemdude.notcord.commons.enums.Scope;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class TokenGenerationService {
    private static final Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

    private static final long accessTokenDefaultExpires = 60 * 60 * 24 * 30 * 2;  // 2 Months
    private static final long refreshTokenDefaultExpires = 60 * 60 * 24 * 30 * 6;  // 6 Months

    private final Oauth2TokenPairRepository oauth2TokenPairRepository;

    public TokenGenerationService(Oauth2TokenPairRepository oauth2TokenPairRepository) {
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
    }

    public String generateToken(User linkedTo) {
        var userIdPart = encoder.encode(linkedTo.getId().getBytes(StandardCharsets.UTF_8));
        var uniquePart = encoder.encode((Thread.currentThread().getName() + System.currentTimeMillis()).getBytes(
            StandardCharsets.UTF_8));
        var randomPart = UUID.randomUUID().toString().replace("-", "").getBytes(StandardCharsets.UTF_8);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(uniquePart);
        digest.update(".".getBytes(StandardCharsets.UTF_8));
        digest.update(randomPart);
        var hashOutput = encoder.encode(digest.digest());

        return new String(userIdPart) + "." + new String(hashOutput);
    }

    public Mono<Oauth2TokenPair> generateUserTokenPairForFrontend(User user) {
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer",
            generateToken(user),
            generateToken(user),
            Instant.now().plusSeconds(accessTokenDefaultExpires),
            Instant.now().plusSeconds(refreshTokenDefaultExpires),
            user.getId(),
            Scope.USER,
            null
        ));
    }

    public Mono<Oauth2TokenPair> generateTokenPair(User user, long scope, long accessTokenExpires, String clientId) {
        return oauth2TokenPairRepository.save(new Oauth2TokenPair(
            "Bearer",
            generateToken(user),
            generateToken(user),
            Instant.now().plusSeconds(accessTokenExpires),
            Instant.now().plusSeconds(refreshTokenDefaultExpires),
            user.getId(),
            scope,
            clientId
        ));
    }
}
