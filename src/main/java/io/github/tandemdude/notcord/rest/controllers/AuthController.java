package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.requests.UserCreateRequestBody;
import io.github.tandemdude.notcord.models.responses.UserResponse;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.utils.EmailSender;
import io.github.tandemdude.notcord.utils.JwtUtil;
import io.github.tandemdude.notcord.utils.PasswordHasher;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;
    private final JwtUtil jwtUtil;

    public AuthController(
        UserRepository userRepository, PasswordHasher passwordHasher, EmailSender emailSender, JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
    }

    public Mono<ResponseEntity<Object>> newUser(@Valid @RequestBody UserCreateRequestBody body) {
        return Mono.just(new User(body.getUsername(), body.getEmail(), passwordHasher.hashPassword(body.getPassword())))
            .flatMap(userRepository::save)
            .doOnNext(user -> {
                var token = jwtUtil.generateToken(Map.of("userId", user.getId()), 60 * 60 * 2);
                String content = "Hi " + body.getUsername() + "!\n\n"
                    + "Thanks for using NotCord - the crappy discord alternative.\n"
                    + "Next step is to verify your email address. Click the below link or paste it into a browser:\n\n"
                    + "http://localhost:8080/auth/users/verifyEmail?token=" + token + "\n\n"
                    + "The link will expire in 2 hours.\n\n"
                    + "Cheers,\n"
                    + "thomm.o";
                emailSender.sendEmailAsync(user.getEmail(), "Notcord Email Verification", content);
            })
            .map(user -> ResponseEntity.ok(UserResponse.from(user)));
    }

    @PostMapping("/users/register")
    @Transactional
    public Mono<ResponseEntity<?>> registerUser(@Valid @RequestBody UserCreateRequestBody body) {
        return Mono.just(body)
            .flatMap(rb -> userRepository.existsByUsername(rb.getUsername()))
            .flatMap(exists -> exists ? Mono.just(ResponseEntity.status(409).build()) : newUser(body));
    }

    @GetMapping("/users/verifyEmail")
    @Transactional
    public Mono<ResponseEntity<String>> verifyUserEmail(@RequestParam String token) {
        var parsed = jwtUtil.parseToken(token);
        if (parsed.isEmpty()) {
            return Mono.just(ResponseEntity.status(401).body("Email not verified"));
        }
        return userRepository.findById((String) parsed.get().get("userId"))
            .doOnNext(user -> user.setEmailVerified(true))
            .flatMap(userRepository::save)
            .thenReturn(ResponseEntity.ok("Email verified"));
    }

    // login
    // logout
    // reset password
    // oauth2 server impl
}
