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

package io.github.tandemdude.notcord.authorizer.components;

import io.github.tandemdude.notcord.authorizer.config.JwtProperties;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(Map<String, Object> claims, long expiresInSeconds) {
        var newClaims = new HashMap<>(claims);
        return Jwts.builder()
            .setClaims(newClaims)
            .setExpiration(new Date(Instant.now().toEpochMilli() + (expiresInSeconds * 1000)))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
            .compact();
    }

    public Optional<Map<String, Object>> parseToken(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException ex) {
            logger.debug("Error occurred while decoding JWT token", ex);
        }
        return Optional.empty();
    }

    public Optional<Map<String, Object>> parseTokenAllowExpired(String token) {
        try {
            return Optional.of(Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            logger.debug("Error occurred while decoding JWT token", ex);
        } catch (ExpiredJwtException ex) {
            return Optional.of(ex.getClaims());
        }
        return Optional.empty();
    }
}
