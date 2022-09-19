package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.requests.UserCreateRequestBody;
import io.github.tandemdude.notcord.models.requests.UserSignInRequestBody;
import io.github.tandemdude.notcord.models.responses.UserResponse;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.utils.EmailSender;
import io.github.tandemdude.notcord.utils.JwtUtil;
import io.github.tandemdude.notcord.utils.PasswordHasher;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Controller
public class FrontendController {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;
    private final JwtUtil jwtUtil;

    public FrontendController(UserRepository userRepository, PasswordHasher passwordHasher, EmailSender emailSender, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/app")
    public String renderApp() {
        return "404";
    }

    @GetMapping("/app/signUp")
    public String renderSignUp(@CookieValue(value = "session", required = false) String existingSession, ServerWebExchange serverWebExchange) {
        Optional<Map<String, Object>> decoded = existingSession == null ? Optional.empty() : jwtUtil.parseToken(existingSession);
        if (decoded.isEmpty()) {
            serverWebExchange.getResponse().addCookie(ResponseCookie.from("session", "").maxAge(0).build());
            return "sign_up";
        }
        return "redirect:/app";
    }

    public Mono<ResponseEntity<Void>> newUser(UserCreateRequestBody body) {
        return Mono.just(new User(body.getUsername(), body.getEmail(), passwordHasher.hashPassword(body.getPassword())))
                .flatMap(userRepository::save)
                .doOnNext(user -> {
                    var token = jwtUtil.generateToken(Map.of("userId", user.getId()), 60 * 60 * 2);
                    String content = "Hi " + body.getUsername() + "!\n\n"
                            + "Thanks for using NotCord - the crappy discord alternative.\n"
                            + "Next step is to verify your email address. Click the below link or paste it into a browser:\n\n"
                            + "http://localhost:8080/app/verifyEmail?token=" + token + "\n\n"
                            + "The link will expire in 2 hours.\n\n"
                            + "Cheers,\n"
                            + "thomm.o";
                    emailSender.sendEmailAsync(user.getEmail(), "Notcord Email Verification", content);
                })
                .map(user -> ResponseEntity.ok().build());
    }

    @PostMapping("/app/signUp")
    public Mono<ResponseEntity<Void>> handleSignUp(@RequestBody UserCreateRequestBody body) {
        return Mono.just(body)
                .flatMap(rb -> userRepository.existsByUsername(rb.getUsername()))
                .flatMap(exists -> exists ? Mono.just(ResponseEntity.status(409).build()) : newUser(body));
    }

    @GetMapping("/app/verifyEmail")
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

    @GetMapping("/app/signIn")
    public String renderSignIn(
            @CookieValue(value = "session", required = false) String existingSession,
            @RequestParam(required = false) String ctx, ServerWebExchange serverWebExchange, Model model
    ) {
        Optional<Map<String, Object>> decoded = existingSession == null ? Optional.empty() : jwtUtil.parseToken(existingSession);
        if (decoded.isEmpty()) {
            model.addAttribute("ctx", ctx);
            serverWebExchange.getResponse().addCookie(ResponseCookie.from("session", "").maxAge(0).build());
            return "sign_in";
        }
        return "redirect:/app";
    }

    @PostMapping("/app/signIn")
    public String handleSignIn(@RequestBody UserSignInRequestBody body, ServerWebExchange serverWebExchange) {
        return "404";
    }
}
