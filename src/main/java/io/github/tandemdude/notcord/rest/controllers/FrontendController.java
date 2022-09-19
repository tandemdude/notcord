package io.github.tandemdude.notcord.rest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tandemdude.notcord.models.db.User;
import io.github.tandemdude.notcord.models.requests.UserCreateRequestBody;
import io.github.tandemdude.notcord.models.requests.UserSignInRequestBody;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.utils.DefaultAvatarGenerator;
import io.github.tandemdude.notcord.utils.EmailSender;
import io.github.tandemdude.notcord.utils.JwtUtil;
import io.github.tandemdude.notcord.utils.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.StreamSupport;

@Controller
public class FrontendController {
    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final EmailSender emailSender;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public FrontendController(UserRepository userRepository, PasswordHasher passwordHasher, EmailSender emailSender, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    public String flashMessage(@Nullable String currentFlashes, String message) {
        try {
            if (currentFlashes == null || currentFlashes.isBlank()) {
                return objectMapper.writeValueAsString(List.of(message));
            }
            var messages = new ArrayList<String>();
            StreamSupport.stream(objectMapper.readTree(currentFlashes).spliterator(), false).map(JsonNode::asText).forEach(messages::add);
            messages.add(message);
            return objectMapper.writeValueAsString(messages);
        } catch (JsonProcessingException ex) {
            logger.warn("Error encoding flashed messages", ex);
            return "";
        }
    }

    public void clearFlashedMessages(ServerWebExchange serverWebExchange) {
        serverWebExchange.getResponse().addCookie(ResponseCookie.from("flashes", "").maxAge(0).build());
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
    public Mono<String> verifyUserEmail(
            @RequestParam("token") String token, Model model
    ) {
        var parsed = jwtUtil.parseToken(token);
        if (parsed.isEmpty()) {
            return Mono.just("email_verification")
                    .doOnNext(s -> model.addAttribute("title", "Uh Oh!")
                            .addAttribute("message", "Your email address could not be verified - the verification link has expired"));
        }
        return userRepository.findById((String) parsed.get().get("userId"))
                .flatMap(user -> user.getEmailVerified() ? Mono.empty() : Mono.just(user))
                .doOnNext(user -> user.setEmailVerified(true))
                .flatMap(userRepository::save)
                .map(user -> "email_verification")
                .doOnNext(s -> model.addAttribute("title", "Success!")
                        .addAttribute("message", "Your email address has been verified successfully"))
                .switchIfEmpty(Mono.just("email_verification")
                        .doOnNext(s -> model.addAttribute("title", "Uh Oh!")
                                .addAttribute("message", "Your email address has already been verified")));
    }

    @GetMapping("/app/signIn")
    public String renderSignIn(
            @CookieValue(value = "session", required = false) String existingSession,
            @RequestParam(required = false) String ref, ServerWebExchange serverWebExchange, Model model
    ) {
        Optional<Map<String, Object>> decoded = existingSession == null ? Optional.empty() : jwtUtil.parseToken(existingSession);
        if (decoded.isEmpty()) {
            model.addAttribute("ref", ref);
            serverWebExchange.getResponse().addCookie(ResponseCookie.from("session", "").maxAge(0).build());
            return "sign_in";
        }
        return "redirect:/app";
    }

    @PostMapping("/app/signIn")
    public String handleSignIn(@RequestBody UserSignInRequestBody body, ServerWebExchange serverWebExchange) {
        return "404";
    }

    @GetMapping("/app/test/{name}")
    public ResponseEntity<String> generateAvatar(@PathVariable String name) {
        return ResponseEntity.ok(DefaultAvatarGenerator.generateDefaultAvatarSvg(name));
    }
}
