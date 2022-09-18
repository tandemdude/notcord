package io.github.tandemdude.notcord.rest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tandemdude.notcord.models.db.Oauth2Credentials;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.repositories.Oauth2CredentialsRepository;
import io.github.tandemdude.notcord.utils.JwtUtil;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@RestController
@RequestMapping("/auth/oauth")
public class Oauth2FLowController {
    private final Oauth2CredentialsRepository oauth2CredentialsRepository;
    private final JwtUtil jwtUtil;

    public Oauth2FLowController(Oauth2CredentialsRepository oauth2CredentialsRepository, JwtUtil jwtUtil) {
        this.oauth2CredentialsRepository = oauth2CredentialsRepository;
        this.jwtUtil = jwtUtil;
    }

    public Mono<ResponseEntity<Object>> handleResponseTypes(String responseType, String clientId, String redirectUri, String scope, String state) {
        if (responseType.equals("authorization_code")) {
            scope = scope == null ? "identity" : scope;
            if (Arrays.stream(scope.split(",")).allMatch(Scope::contains)) {
                return Mono.just(ResponseEntity.badRequest()
                        .body(new AuthorizationError("invalid_scope", "One or more scopes were not recognised")));
            }

            // TODO - jwt cannot contain any null values in claims
//            var session = jwtUtil.generateToken(Map.of("clientId", clientId, "redirectUri", redirectUri, "scope", scope, "state", state), 60 * 60 * 15);
            return oauth2CredentialsRepository.findById(clientId)
                    .map(Oauth2Credentials::getRedirectUri)
                    .flatMap(uri -> uri.equals(redirectUri) ? Mono.just(uri) : Mono.empty())
                    .map(uri -> ResponseEntity.ok().build()) // Everything ok present auth prompt
                    .switchIfEmpty(Mono.just(ResponseEntity.badRequest()
                            .body(new AuthorizationError("invalid_request", "The 'redirect_uri' parameter is invalid"))));
        }
        return Mono.just(ResponseEntity.status(400)
                .body(new AuthorizationError("unsupported_response_type", "Response type '" + responseType + "' is not permitted")));
    }

    @PostMapping("/authorize")
    public Mono<ResponseEntity<?>> oauth2Authorize(
            @RequestParam("response_type") String responseType, @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri, @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state
    ) {
        return oauth2CredentialsRepository.existsById(clientId)
                .flatMap(exists -> exists ? handleResponseTypes(responseType, clientId, redirectUri, scope, state)
                        : Mono.just(ResponseEntity.status(400).body(new AuthorizationError("invalid_request", "The 'client_id' parameter is invalid"))));
    }

    @PostMapping("/token")
    public Mono<ResponseEntity<?>> oauth2Token() {
        return Mono.just(ResponseEntity.notFound().build());
    }

    @Data
    public static class AuthorizationError {
        private final String error;
        @JsonProperty("error_description")
        private final String errorDescription;
    }
}
