package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.db.Oauth2AuthorizationCode;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.requests.Oauth2TokenRequestBody;
import io.github.tandemdude.notcord.models.responses.AuthorizationErrorResponse;
import io.github.tandemdude.notcord.repositories.Oauth2AuthorizationCodeRepository;
import io.github.tandemdude.notcord.repositories.Oauth2CredentialsRepository;
import io.github.tandemdude.notcord.utils.JwtUtil;
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
    private final Oauth2AuthorizationCodeRepository oauth2AuthorizationCodeRepository;
    private final Oauth2CredentialsRepository oauth2CredentialsRepository;
    private final JwtUtil jwtUtil;

    public Oauth2FlowController(Oauth2AuthorizationCodeRepository oauth2AuthorizationCodeRepository, Oauth2CredentialsRepository oauth2CredentialsRepository, JwtUtil jwtUtil) {
        this.oauth2AuthorizationCodeRepository = oauth2AuthorizationCodeRepository;
        this.oauth2CredentialsRepository = oauth2CredentialsRepository;
        this.jwtUtil = jwtUtil;
    }

    public Long forceLong(Object value) {
        return Long.parseLong(String.valueOf(value));
    }

    public Mono<ResponseEntity<Object>> handleResponseTypes(
        String responseType, String clientId, String redirectUri, String scope, String state
    ) {
        if (responseType.equals("authorization_code")) {
            if (!Arrays.stream(scope.split(" ")).allMatch(Scope.getScopeValueMap()::containsKey)) {
                return Mono.just(ResponseEntity.badRequest()
                    .body(new AuthorizationErrorResponse("invalid_scope", "One or more scopes were not recognised")));
            }

            return oauth2CredentialsRepository.findById(clientId)
                    .flatMap(client -> client.getRedirectUri().equals(redirectUri) ? Mono.just(client) : Mono.empty())
                    .map(client -> {
                        Map<String, Object> claims;
                        var scopeBitfield = Scope.bitfieldFromScopes(Arrays.asList(scope.split(" ")));
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
                    .map(token -> ResponseEntity.status(302).header("Location", "http://localhost:3000/app/oauth?returnTo=http://localhost:8080/oauth/prompt/" + token).build())
                    .defaultIfEmpty(ResponseEntity.badRequest()
                            .body(new AuthorizationErrorResponse("invalid_request", "The 'redirect_uri' parameter is invalid")));
        }
        return Mono.just(ResponseEntity.status(400)
            .body(new AuthorizationErrorResponse(
                "unsupported_response_type",
                "Response type '" + responseType + "' is not permitted"
            )));
    }

    @GetMapping("/authorize")
    public Mono<ResponseEntity<Object>> oauth2Authorize(
            @RequestParam("response_type") String responseType, @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri, @RequestParam(value = "scope", required = false, defaultValue = "identity.read") String scope,
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
                .body(new AuthorizationErrorResponse("invalid_request", "The 'client_id' parameter is invalid"))));
    }

    @GetMapping("/prompt/{token}")
    public String displayConsentScreen(@PathVariable String token, @RequestParam String userToken, Model model) {
        var maybeToken = jwtUtil.parseToken(token);
        if (maybeToken.isEmpty()) {
            return "redirect:http://localhost:3000/404";
        }

        var allowToken = jwtUtil.generateToken(Map.of("inner", token, "consent", "allow"), 60 * 15);
        var denyToken = jwtUtil.generateToken(Map.of("inner", token, "consent", "deny"), 60 * 15);

        var decoded = maybeToken.get();
        model.addAttribute("userToken", userToken);
        model.addAttribute("allowToken", allowToken);
        model.addAttribute("denyToken", denyToken);
        model.addAttribute("appName", decoded.get("appName"));
        model.addAttribute("scopes", Scope.scopesFromBitfield(forceLong(decoded.get("scope"))));
        model.addAttribute("redirectUri", decoded.get("redirectUri"));
        model.addAttribute("appName", decoded.get("appName"));

        return "authorize";
    }

    @GetMapping("/complete")
    public Mono<String> completeAuthentication(@RequestParam String token, @RequestParam String userToken) {
        var decodedMeta = jwtUtil.parseToken(token);
        if (decodedMeta.isEmpty()) {
            // Invalid token has been supplied
            return Mono.just("redirect:http://localhost:3000/404");
        }

        var inner = jwtUtil.parseToken((String) decodedMeta.get().get("inner"));
        if (inner.isEmpty()) {
            // The token supplied was valid but not of the correct type
            return Mono.just("redirect:http://localhost:3000/404");
        }

        var innerClaims = inner.get();
        if (!innerClaims.containsKey("redirectUri") || !innerClaims.containsKey("clientId") || !innerClaims.containsKey("scope")) {
            // Inner token does not contain the required data
            return Mono.just("redirect:http://localhost:3000/404");
        }

        var redirectUriBuilder = UriComponentsBuilder.fromUriString((String) innerClaims.get("redirectUri"));
        redirectUriBuilder.fragment(null);

        // User did not permit access
        if (!"allow".equals(decodedMeta.get().get("consent"))) {
            redirectUriBuilder.queryParam("error", "access_denied");
            if (innerClaims.get("state") != null) {
                redirectUriBuilder.queryParam("state", innerClaims.get("state"));
            }
            return Mono.just("redirect:" + redirectUriBuilder.build());
        }

        // The given user token does not have permissions to grant access to an application or was invalid
        var decodedUserToken = jwtUtil.parseToken(userToken);
        if (decodedUserToken.isEmpty() || !Scope.grantsAll(forceLong(decodedUserToken.get().get("scope")), Scope.USER)) {
            redirectUriBuilder.queryParam("error", "access_denied");
            if (innerClaims.get("state") != null) {
                redirectUriBuilder.queryParam("state", innerClaims.get("state"));
            }
            return Mono.just("redirect:" + redirectUriBuilder.build());
        }
        var userTokenClaims = decodedUserToken.get();

        // Access has been granted, so we return a code which can be exchanged for tokens later
        return oauth2AuthorizationCodeRepository.save(new Oauth2AuthorizationCode(
                (String) userTokenClaims.get("userId"),
                (String) innerClaims.get("clientId"),
                forceLong(innerClaims.get("scope"))
            ))
            .map(code -> {
                redirectUriBuilder.queryParam("code", code.getCode());
                if (innerClaims.get("state") != null) {
                    redirectUriBuilder.queryParam("state", innerClaims.get("state"));
                }
                return redirectUriBuilder.build();
            })
            .map(url -> "redirect:" + url);
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<ResponseEntity<?>> oauth2Token(@Valid @RequestBody Oauth2TokenRequestBody body) {
        // TODO - access token grant and token refreshing
        return Mono.just(ResponseEntity.notFound().build());
    }
}
