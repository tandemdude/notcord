package io.github.tandemdude.notcord.rest.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.requests.Oauth2TokenRequestBody;
import io.github.tandemdude.notcord.repositories.Oauth2CredentialsRepository;
import io.github.tandemdude.notcord.utils.JwtUtil;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/oauth")
public class Oauth2FlowController {
    private final Oauth2CredentialsRepository oauth2CredentialsRepository;
    private final JwtUtil jwtUtil;

    public Oauth2FlowController(Oauth2CredentialsRepository oauth2CredentialsRepository, JwtUtil jwtUtil) {
        this.oauth2CredentialsRepository = oauth2CredentialsRepository;
        this.jwtUtil = jwtUtil;
    }

    public Mono<ResponseEntity<Object>> handleResponseTypes(
        String responseType, String clientId, String redirectUri, String scope, String state
    ) {
        if (responseType.equals("authorization_code")) {
            var finalScope = scope == null ? "identify" : scope;
            if (Arrays.stream(finalScope.split(" ")).allMatch(Scope.getScopeValueMap()::containsKey)) {
                return Mono.just(ResponseEntity.badRequest()
                    .body(new AuthorizationError("invalid_scope", "One or more scopes were not recognised")));
            }

            return oauth2CredentialsRepository.findById(clientId)
                    .flatMap(client -> client.getRedirectUri().equals(redirectUri) ? Mono.just(client) : Mono.empty())
                    .map(client -> {
                        Map<String, Object> claims;
                        var scopeBitfield = Scope.bitfieldFromScopes(Arrays.asList(finalScope.split(" ")));
                        if (state != null) {
                            claims = Map.of(
                                    "clientId", clientId, "redirectUri", redirectUri,
                                    "scope", scopeBitfield, "state", state, "appName", client.getAppName()
                            );
                        } else {
                            claims = Map.of(
                                    "clientId", clientId, "redirectUri", redirectUri,
                                    "scope", scopeBitfield, "appName", client.getAppName());
                        }
                        return jwtUtil.generateToken(claims, 60 * 60 * 15);
                    })
                    .map(token -> ResponseEntity.status(302).header("Location", "http://localhost:3000/app?returnTo=http://localhost:8080/oauth/prompt/" + token).build())
                    .switchIfEmpty(Mono.just(ResponseEntity.badRequest()
                            .body(new AuthorizationError("invalid_request", "The 'redirect_uri' parameter is invalid"))));
        }
        return Mono.just(ResponseEntity.status(400)
            .body(new AuthorizationError(
                "unsupported_response_type",
                "Response type '" + responseType + "' is not permitted"
            )));
    }

    @GetMapping("/authorize")
    public Mono<ResponseEntity<Object>> oauth2Authorize(
            @RequestParam("response_type") String responseType, @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri, @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state
    ) {
        return oauth2CredentialsRepository.existsById(clientId)
            .flatMap(exists -> exists ? handleResponseTypes(
                responseType,
                clientId,
                redirectUri,
                scope,
                state
            ) : Mono.just(ResponseEntity.status(400)
                .body(new AuthorizationError("invalid_request", "The 'client_id' parameter is invalid"))));
    }

    @GetMapping("/prompt/{token}")
    public String displayConsentScreen(@PathVariable String token, @RequestParam("user") String userToken, Model model) {
        var decoded = jwtUtil.parseToken(token);
        if (decoded.isEmpty()) {
            return "redirect:http://localhost:3000/404";
        }

        var allowToken = jwtUtil.generateToken(Map.of("inner", token, "consent", "allow"), 60 * 15);
        var denyToken = jwtUtil.generateToken(Map.of("inner", token, "consent", "deny"), 60 * 15);

        model.addAttribute("allowToken", allowToken);
        model.addAttribute("denyToken", denyToken);
        model.addAttribute("appName", decoded.get().get("appName"));
        model.addAttribute("scopes", ((String) decoded.get().get("scope")).split(" "));
        model.addAttribute("redirectUri", decoded.get().get("redirectUri"));
        model.addAttribute("appName", decoded.get().get("appName"));

        return "authorize";
    }

    @GetMapping("/complete")
    public String completeAuthentication(@RequestParam String token) {
        var decoded = jwtUtil.parseToken(token);
        if (decoded.isEmpty()) {
            return "redirect:http://localhost:3000/404";
        }
        var inner = jwtUtil.parseToken((String) decoded.get().get("inner"));
        if (inner.isEmpty()) {
            return "redirect:http://localhost:3000/404";
        }

        if (!"allow".equals(decoded.get().get("consent"))) {
            var uri = UriComponentsBuilder.fromUriString((String) inner.get().get("redirectUri"))
                    .queryParam("error", "access_denied")
                    .fragment(null);
            if (inner.get().get("state") != null) {
                uri.queryParam("state", inner.get().get("state"));
            }
            return "redirect:" + uri.build();
        }
        // TODO - authorize
        return "redirect:http://localhost:3000/404";
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<ResponseEntity<?>> oauth2Token(@Valid @RequestBody Oauth2TokenRequestBody body) {
        // TODO - access token grant and token refreshing
        return Mono.just(ResponseEntity.notFound().build());
    }

    @Data
    public static class AuthorizationError {
        private final String error;
        @JsonProperty("error_description")
        private final String errorDescription;
    }
}
