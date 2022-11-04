package io.github.tandemdude.notcord.authorizer.controllers;

import io.github.tandemdude.notcord.authorizer.components.JwtUtil;
import io.github.tandemdude.notcord.authorizer.config.EndpointConfig;
import io.github.tandemdude.notcord.authorizer.exceptions.*;
import io.github.tandemdude.notcord.authorizer.models.db.Oauth2AuthorizationCode;
import io.github.tandemdude.notcord.authorizer.models.requests.Oauth2TokenRequestBody;
import io.github.tandemdude.notcord.authorizer.models.responses.Oauth2TokenInfoResponse;
import io.github.tandemdude.notcord.authorizer.models.responses.Oauth2TokenResponse;
import io.github.tandemdude.notcord.authorizer.models.responses.OauthErrorResponse;
import io.github.tandemdude.notcord.authorizer.repositories.Oauth2AuthorizationCodeRepository;
import io.github.tandemdude.notcord.authorizer.repositories.Oauth2CredentialsRepository;
import io.github.tandemdude.notcord.authorizer.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.authorizer.services.Oauth2AuthorizerService;
import io.github.tandemdude.notcord.commons.enums.Scope;
import io.github.tandemdude.notcord.commons.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.commons.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/oauth")
public class Oauth2FlowController {
    private final Oauth2AuthorizationCodeRepository oauth2AuthorizationCodeRepository;
    private final Oauth2CredentialsRepository oauth2CredentialsRepository;
    private final Oauth2AuthorizerService oauth2AuthorizerService;
    private final UserRepository userRepository;
    private final Oauth2TokenPairRepository oauth2TokenPairRepository;
    private final EndpointConfig endpointConfig;
    private final JwtUtil jwtUtil;

    public Oauth2FlowController(
        Oauth2AuthorizationCodeRepository oauth2AuthorizationCodeRepository,
        Oauth2CredentialsRepository oauth2CredentialsRepository,
        Oauth2AuthorizerService oauth2AuthorizerService,
        UserRepository userRepository,
        Oauth2TokenPairRepository oauth2TokenPairRepository,
        EndpointConfig endpointConfig,
        JwtUtil jwtUtil
    ) {
        this.oauth2AuthorizationCodeRepository = oauth2AuthorizationCodeRepository;
        this.oauth2CredentialsRepository = oauth2CredentialsRepository;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
        this.userRepository = userRepository;
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
        this.endpointConfig = endpointConfig;
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
                    .body(new OauthErrorResponse("invalid_scope", "One or more scopes were not recognised")));
            }

            return oauth2CredentialsRepository.findById(clientId).filter(client -> client.getRedirectUri()
                .equals(redirectUri)).map(client -> {
                Map<String, Object> claims;
                var scopeBitfield = Scope.bitfieldFromScopes(Arrays.asList(scope.split(" ")));
                if (state != null) {
                    claims = Map.of(
                        "clientId",
                        clientId,
                        "redirectUri",
                        redirectUri,
                        "scope",
                        scopeBitfield,
                        "state",
                        state
                    );
                }
                else {
                    claims = Map.of("clientId", clientId, "redirectUri", redirectUri, "scope", scopeBitfield);
                }
                return jwtUtil.generateToken(claims, 60 * 60 * 15);
            }).map(token -> ResponseEntity.status(302)
                .header(
                    "Location",
                    endpointConfig.cleanFrontendUrl() + "/app/oauth?returnTo=" + endpointConfig.cleanAuthorizerUrl() + "/oauth/prompt/" + token
                )
                .build()).defaultIfEmpty(ResponseEntity.badRequest()
                .body(new OauthErrorResponse("invalid_request", "The 'redirect_uri' parameter is invalid")));
        }
        return Mono.just(ResponseEntity.status(400)
            .body(new OauthErrorResponse(
                "unsupported_response_type",
                "Response type '" + responseType + "' is not permitted"
            )));
    }

    @GetMapping("/authorize")
    public Mono<ResponseEntity<Object>> oauth2Authorize(
        @RequestParam("response_type") String responseType,
        @RequestParam("client_id") String clientId,
        @RequestParam("redirect_uri") String redirectUri,
        @RequestParam(value = "scope", required = false, defaultValue = "identity.read") String scope,
        @RequestParam(value = "state", required = false) String state
    ) {
        return oauth2CredentialsRepository.existsById(clientId).flatMap(exists -> exists ? handleResponseTypes(
            responseType,
            clientId,
            redirectUri,
            scope,
            state
        ) : Mono.just(ResponseEntity.status(400)
            .body(new OauthErrorResponse("invalid_request", "The 'client_id' parameter is invalid"))));
    }

    @GetMapping("/prompt/{token}")
    public Mono<String> displayConsentScreen(@PathVariable String token, @RequestParam String userToken, Model model) {
        var maybeClaims = jwtUtil.parseToken(token);
        if (maybeClaims.isEmpty()) {
            return Mono.just("redirect:" + endpointConfig.frontend404Page());
        }

        var allowToken = jwtUtil.generateToken(Map.of("inner", token, "consent", "allow"), 60 * 15);
        var denyToken = jwtUtil.generateToken(Map.of("inner", token, "consent", "deny"), 60 * 15);

        var claims = maybeClaims.get();
        model.addAttribute("userToken", userToken);
        model.addAttribute("allowToken", allowToken);
        model.addAttribute("denyToken", denyToken);
        model.addAttribute("redirectUri", claims.get("redirectUri"));
        model.addAttribute("frontendBaseUrl", endpointConfig.cleanFrontendUrl());

        var requestedScopes = forceLong(claims.get("scope"));
        model.addAttribute("scopes", Scope.getScopeDescriptionMap().entrySet().stream()
            .filter(entry -> (entry.getKey() & requestedScopes) == entry.getKey())
            .map(Map.Entry::getValue)
            .toList()
        );

        var maybeUserClaims = jwtUtil.parseToken(userToken);
        var userClaims = maybeUserClaims.orElse(Collections.emptyMap());

        return userRepository.findById((String) userClaims.get("userId"))
            .doOnNext(user -> model.addAttribute("userIcon", user.getDefaultAvatarSvg()))
            .doOnNext(user -> model.addAttribute("userName", user.getUsername()))
            .flatMap(unused -> oauth2CredentialsRepository.findById((String) claims.get("clientId")))
            .doOnNext(creds -> model.addAttribute("appIcon", creds.getDefaultIconSvg()))
            .doOnNext(creds -> model.addAttribute("appName", creds.getAppName()))
            .map(unused -> "authorize")
            .defaultIfEmpty("redirect:" + endpointConfig.frontend404Page());  // User token or prompt token is unrecognised
    }

    @GetMapping("/complete")
    public Mono<String> completeAuthentication(@RequestParam String token, @RequestParam String userToken) {
        var decodedMeta = jwtUtil.parseToken(token);
        if (decodedMeta.isEmpty()) {
            // Invalid token has been supplied
            return Mono.just("redirect:" + endpointConfig.frontend404Page());
        }

        var inner = jwtUtil.parseToken((String) decodedMeta.get().get("inner"));
        if (inner.isEmpty()) {
            // The token supplied was valid but not of the correct type
            return Mono.just("redirect:" + endpointConfig.frontend404Page());
        }

        var innerClaims = inner.get();
        if (!innerClaims.containsKey("redirectUri") || !innerClaims.containsKey("clientId") || !innerClaims.containsKey(
            "scope")) {
            // Inner token does not contain the required data
            return Mono.just("redirect:" + endpointConfig.frontend404Page());
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
        if (decodedUserToken.isEmpty() || !Scope.grantsAll(
            forceLong(decodedUserToken.get().get("scope")),
            Scope.USER
        )) {
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
        )).map(code -> {
            redirectUriBuilder.queryParam("code", code.getCode());
            if (innerClaims.get("state") != null) {
                redirectUriBuilder.queryParam("state", innerClaims.get("state"));
            }
            return redirectUriBuilder.build();
        }).map(url -> "redirect:" + url);
    }

    @Transactional
    public Mono<ResponseEntity<Object>> handleGrantTypes(Oauth2TokenRequestBody body) {
        if ("authorization_code".equals(body.getGrant_type())) {
            return oauth2AuthorizationCodeRepository.findByCodeAndClientId(body.getCode(), body.getClient_id())
                .switchIfEmpty(Mono.error(AuthorizationCodeDoesNotExistException::new))
                // Check if code has expired
                .filter(code -> Instant.now().isBefore(code.getExpiresAt()))
                .switchIfEmpty(Mono.error(AuthorizationCodeExpiredException::new))
                .flatMap(code -> oauth2CredentialsRepository.findById(code.getClientId())
                    // Check that the provided client secret matches the one for the code's client ID
                    .filter(creds -> creds.getClientSecret().equals(body.getClient_secret()))
                    .switchIfEmpty(Mono.error(CredentialsDoNotMatchException::new))
                    // Check that the provided redirect URI matches the one for the code's client ID
                    .flatMap(creds -> creds.getRedirectUri()
                        .equals(body.getRedirect_uri()) ? Mono.just(code) : Mono.empty())
                    .switchIfEmpty(Mono.error(RedirectUriIncorrectException::new)))
                .flatMap(code -> userRepository.findById(code.getUserId())
                    // Issue new access token and refresh token pair
                    .flatMap(user -> oauth2AuthorizerService.generateTokenPair(
                        user,
                        code.getScope(),
                        43200,
                        code.getClientId()
                    ))
                    // Delete the used authorization code from the database
                    .flatMap(pair -> oauth2AuthorizationCodeRepository.delete(code).thenReturn(pair)))
                .map(pair -> ResponseEntity.ok(Oauth2TokenResponse.from(pair)));
        }
        else if ("refresh_token".equals(body.getGrant_type())) {
            return oauth2TokenPairRepository.findByRefreshToken(body.getRefresh_token())
                .switchIfEmpty(Mono.error(RefreshTokenDoesNotExistException::new))
                // Check if refresh token has expired
                .filter(pair -> jwtUtil.parseToken(pair.getRefreshToken()).isPresent())
                .switchIfEmpty(Mono.error(RefreshTokenExpiredException::new))
                // Check that the provided client ID matches the one for the token pair
                .filter(pair -> Objects.equals(pair.getClientId(), body.getClient_id()))
                .switchIfEmpty(Mono.error(RefreshTokenDoesNotExistException::new))
                .flatMap(pair -> oauth2CredentialsRepository.findById(body.getClient_id())
                    // Check that the provided client secret matches the one for the token pair's client ID
                    .filter(creds -> creds.getClientSecret().equals(body.getClient_secret()))
                    .switchIfEmpty(Mono.error(CredentialsDoNotMatchException::new))
                    // We don't actually need to know the credentials, so we just return out the original token pair again
                    .map(creds -> pair))
                .flatMap(pair -> userRepository.findById(pair.getUserId())
                    // Issue new access token and refresh token pair
                    .flatMap(user -> oauth2AuthorizerService.generateTokenPair(
                        user,
                        pair.getScope(),
                        43200,
                        pair.getClientId()
                    ))
                    // Delete the old token pair from the database
                    .flatMap(newPair -> oauth2TokenPairRepository.delete(pair).thenReturn(newPair)))
                .map(pair -> ResponseEntity.ok(Oauth2TokenResponse.from(pair)));
        }

        return Mono.just(ResponseEntity.status(400)
            .body(new OauthErrorResponse(
                "unsupported_grant_type",
                "Grant type '" + body.getGrant_type() + "' is not permitted"
            )));
    }

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<ResponseEntity<Object>> oauth2Token(@Valid Oauth2TokenRequestBody body) {
        return handleGrantTypes(body)
            // Handle errors that could have been caused during authorization code token grant
            .onErrorReturn(
                AuthorizationCodeDoesNotExistException.class,
                ResponseEntity.status(400)
                    .body(new OauthErrorResponse("invalid_grant", "The provided 'code' is invalid"))
            )
            .onErrorReturn(
                AuthorizationCodeExpiredException.class,
                ResponseEntity.status(400)
                    .body(new OauthErrorResponse("invalid_grant", "The provided 'code' is invalid"))
            )
            .onErrorReturn(
                CredentialsDoNotMatchException.class,
                ResponseEntity.status(401)
                    .body(new OauthErrorResponse(
                        "invalid_client",
                        "The provided 'client_id' and 'client_secret' combination is invalid"
                    ))
            )
            .onErrorReturn(
                RedirectUriIncorrectException.class,
                ResponseEntity.status(400)
                    .body(new OauthErrorResponse("invalid_grant", "The provided 'redirect_uri' is invalid"))
            )
            // Handle errors that could have been caused during refresh token grant
            .onErrorReturn(
                RefreshTokenDoesNotExistException.class,
                ResponseEntity.status(400)
                    .body(new OauthErrorResponse("invalid_grant", "The provided 'refresh_token' is invalid"))
            )
            .onErrorReturn(
                RefreshTokenExpiredException.class,
                ResponseEntity.status(400)
                    .body(new OauthErrorResponse("invalid_grant", "The provided 'refresh_token' is invalid"))
            )
            // If for some reason some other error was thrown, or we still have an empty mono
            .defaultIfEmpty(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/token_info")
    public Mono<Oauth2TokenInfoResponse> getTokenInfo(@RequestParam String token) {
        return oauth2TokenPairRepository.findByAccessToken(token)
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.resourceNotFoundException("Token does not exist")))
            .map(Oauth2TokenInfoResponse::from);
    }
}
