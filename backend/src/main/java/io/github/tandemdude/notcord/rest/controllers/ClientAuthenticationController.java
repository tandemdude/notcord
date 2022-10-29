package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.config.EndpointConfig;
import io.github.tandemdude.notcord.exceptions.HttpExceptionFactory;
import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.requests.UserCreateRequestBody;
import io.github.tandemdude.notcord.models.requests.UserSignInRequestBody;
import io.github.tandemdude.notcord.models.responses.Oauth2TokenResponse;
import io.github.tandemdude.notcord.repositories.Oauth2TokenPairRepository;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.rest.services.Oauth2AuthorizerService;
import io.github.tandemdude.notcord.utils.EmailSender;
import io.github.tandemdude.notcord.utils.JwtUtil;
import io.github.tandemdude.notcord.utils.PasswordHasher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

// TODO - replace error response entities with Mono.error
@Controller
@RequestMapping("/client")
public class ClientAuthenticationController {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;
    private final JwtUtil jwtUtil;
    private final Oauth2AuthorizerService oauth2AuthorizerService;
    private final Oauth2TokenPairRepository oauth2TokenPairRepository;
    private final EndpointConfig endpointConfig;

    public ClientAuthenticationController(
        UserRepository userRepository,
        PasswordHasher passwordHasher,
        EmailSender emailSender,
        JwtUtil jwtUtil,
        Oauth2AuthorizerService oauth2AuthorizerService,
        Oauth2TokenPairRepository oauth2TokenPairRepository,
        EndpointConfig endpointConfig
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
        this.oauth2TokenPairRepository = oauth2TokenPairRepository;
        this.endpointConfig = endpointConfig;
    }

    public Mono<ResponseEntity<Void>> newUser(UserCreateRequestBody body) {
        return Mono.just(new User(body.getUsername(), body.getEmail(), passwordHasher.hashPassword(body.getPassword())))
            .flatMap(userRepository::save)
            .doOnNext(user -> {
                var token = jwtUtil.generateToken(Map.of("userId", user.getId()), 60 * 60 * 2);
                String content = "Hi " + body.getUsername() + "!\n\n"
                    + "Thanks for using NotCord - the crappy discord alternative.\n"
                    + "Next step is to verify your email address. Click the below link or paste it into a browser:\n\n"
                    + endpointConfig.cleanFrontendUrl() + "/app/verify-email?token=" + token + "\n\n"
                    + "The link will expire in 2 hours.\n\n"
                    + "Cheers,\n"
                    + "thomm.o";
                emailSender.sendEmailAsync(user.getEmail(), "Notcord Email Verification", content);
            })
            .map(user -> ResponseEntity.ok().build());
    }

    @PostMapping("/sign-up")
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

    @PostMapping("/sign-in")
    public Mono<ResponseEntity<Oauth2TokenResponse>> handleSignIn(@Valid @RequestBody UserSignInRequestBody body) {
        // TODO - we might want to protect this using CORS
        return userRepository.findByEmail(body.getEmail())
            .flatMap(user -> passwordHasher.comparePasswords(
                body.getPassword(),
                user.getPassword()
            ) ? Mono.just(user) : Mono.empty())
            .flatMap(oauth2AuthorizerService::generateUserTokenPairForFrontend)
            .map(tokenPair -> ResponseEntity.ok(Oauth2TokenResponse.from(tokenPair)))
            .defaultIfEmpty(ResponseEntity.status(401).build());
    }

    @PostMapping("/verify-email")
    @Transactional
    public Mono<ResponseEntity<Object>> verifyUserEmail(@RequestParam String token) {
        var parsed = jwtUtil.parseToken(token);
        if (parsed.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).build());
        }
        return userRepository.findById((String) parsed.get().get("userId"))
            .flatMap(user -> user.getEmailVerified() ? Mono.empty() : Mono.just(user))
            .doOnNext(user -> user.setEmailVerified(true))
            .flatMap(userRepository::save)
            .map(user -> ResponseEntity.status(202).build())
            .defaultIfEmpty(ResponseEntity.status(409).build());
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<Oauth2TokenResponse>> refreshUserToken(@RequestParam String token) {
        var parsed = jwtUtil.parseToken(token);
        if (parsed.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).build());
        }
        var data = parsed.get();
        if (!Objects.equals(data.get("type"), "refresh") || data.get("for") == null) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        var accessTokenClaims = jwtUtil.parseTokenAllowExpired((String) data.get("for"));
        if (accessTokenClaims.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        return oauth2TokenPairRepository.findByRefreshToken(token)
            .flatMap(pair -> pair.getScope() == Scope.USER ? Mono.just(pair) : Mono.empty())
            .flatMap(pair -> oauth2TokenPairRepository.delete(pair).thenReturn(pair))
            .flatMap(pair -> userRepository.findById(pair.getUserId()))
            .flatMap(oauth2AuthorizerService::generateUserTokenPairForFrontend)
            .map(Oauth2TokenResponse::from)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.status(401).build());
    }
}
