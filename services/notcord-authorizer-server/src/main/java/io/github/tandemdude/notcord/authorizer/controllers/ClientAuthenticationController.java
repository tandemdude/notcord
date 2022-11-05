package io.github.tandemdude.notcord.authorizer.controllers;

import io.github.tandemdude.notcord.authorizer.components.EmailSender;
import io.github.tandemdude.notcord.authorizer.components.JwtUtil;
import io.github.tandemdude.notcord.authorizer.components.PasswordHasher;
import io.github.tandemdude.notcord.authorizer.config.EndpointProperties;
import io.github.tandemdude.notcord.authorizer.models.requests.UserCreateRequestBody;
import io.github.tandemdude.notcord.authorizer.models.requests.UserSignInRequestBody;
import io.github.tandemdude.notcord.authorizer.models.responses.Oauth2TokenResponse;
import io.github.tandemdude.notcord.authorizer.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.authorizer.repositories.UserRepository;
import io.github.tandemdude.notcord.authorizer.services.Oauth2AuthorizerService;
import io.github.tandemdude.notcord.commons.entities.User;
import io.github.tandemdude.notcord.commons.enums.Scope;
import io.github.tandemdude.notcord.commons.exceptions.HttpExceptionFactory;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/client")
public class ClientAuthenticationController {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;
    private final JwtUtil jwtUtil;
    private final Oauth2AuthorizerService oauth2AuthorizerService;
    private final Oauth2TokenPairRepository oauth2TokenPairRepository;
    private final EndpointProperties endpointProperties;

    public ClientAuthenticationController(
        UserRepository userRepository,
        PasswordHasher passwordHasher,
        EmailSender emailSender,
        JwtUtil jwtUtil,
        Oauth2AuthorizerService oauth2AuthorizerService,
        Oauth2TokenPairRepository oauth2TokenPairRepository,
        EndpointProperties endpointProperties
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
        this.endpointProperties = endpointProperties;
    }

    public Mono<ResponseEntity<Void>> newUser(UserCreateRequestBody body) {
        return Mono.just(new User(body.getUsername(), body.getEmail(), passwordHasher.hashPassword(body.getPassword())))
            .flatMap(userRepository::save)
            .doOnNext(user -> {
                var token = jwtUtil.generateToken(Map.of("userId", user.getId()), 60 * 60 * 2);
                String content = "Hi " + body.getUsername() + "!\n\n"
                    + "Thanks for using NotCord - the crappy discord alternative.\n"
                    + "Next step is to verify your email address. Click the below link or paste it into a browser:\n\n"
                    + endpointProperties.cleanFrontendUrl() + "/app/verify-email?token=" + token + "\n\n"
                    + "The link will expire in 2 hours.\n\n"
                    + "Cheers,\n"
                    + "thomm.o";
                emailSender.sendEmailAsync(user.getEmail(), "Notcord Email Verification", content);
            })
            .map(user -> ResponseEntity.ok().build());
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> handleSignUp(@Valid @RequestBody UserCreateRequestBody body) {
        // TODO - we might want to protect this using CORS
        // TODO - validation (/^[\w\-.]{5,40}$/)
        return Mono.just(body)
            .flatMap(rb -> userRepository.existsByUsername(rb.getUsername()))
            .filter(exists -> !exists)
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.conflictException(
                "A user with that username already exists")))
            .flatMap(unused -> newUser(body));
    }

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Oauth2TokenResponse>> handleSignIn(@Valid @RequestBody UserSignInRequestBody body) {
        // TODO - we might want to protect this using CORS
        return userRepository.findByEmail(body.getEmail())
            .filter(user -> passwordHasher.comparePasswords(body.getPassword(), user.getPassword()))
            .flatMap(oauth2AuthorizerService::generateUserTokenPairForFrontend)
            .map(Oauth2TokenResponse::from)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.status(401).build());
    }

    @PostMapping("/verify-email")
    @Transactional
    public Mono<ResponseEntity<Void>> verifyUserEmail(@RequestParam String token) {
        var parsed = jwtUtil.parseToken(token);
        if (parsed.isEmpty()) {
            return Mono.error(HttpExceptionFactory::invalidTokenException);
        }
        return userRepository.findById((String) parsed.get().get("userId"))
            .flatMap(user -> user.getEmailVerified() ? Mono.empty() : Mono.just(user))
            .doOnNext(user -> user.setEmailVerified(true))
            .flatMap(userRepository::save)
            .map(user -> ResponseEntity.status(202).<Void>build())
            .switchIfEmpty(Mono.error(() -> HttpExceptionFactory.conflictException(null)));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<Oauth2TokenResponse>> refreshUserToken(@RequestParam String token) {
        return oauth2TokenPairRepository.findByRefreshToken(token)
            .flatMap(pair -> pair.getScope() == Scope.USER ? Mono.just(pair) : Mono.empty())
            .flatMap(pair -> oauth2TokenPairRepository.delete(pair).thenReturn(pair.getUserId()))
            .flatMap(userRepository::findById)
            .flatMap(oauth2AuthorizerService::generateUserTokenPairForFrontend)
            .map(Oauth2TokenResponse::from)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.status(401).build());
    }
}
