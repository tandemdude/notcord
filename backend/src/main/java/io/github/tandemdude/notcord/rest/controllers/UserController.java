package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.exceptions.auth.MissingRequiredPermissionException;
import io.github.tandemdude.notcord.exceptions.users.UserDoesNotExistException;
import io.github.tandemdude.notcord.models.oauth2.Scope;
import io.github.tandemdude.notcord.models.responses.UserResponse;
import io.github.tandemdude.notcord.repositories.UserRepository;
import io.github.tandemdude.notcord.rest.services.Oauth2AuthorizerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final Oauth2AuthorizerService oauth2AuthorizerService;

    public UserController(UserRepository userRepository, Oauth2AuthorizerService oauth2AuthorizerService) {
        this.userRepository = userRepository;
        this.oauth2AuthorizerService = oauth2AuthorizerService;
    }

    @GetMapping("/{userId:[1-9][0-9]+}")
    public Mono<ResponseEntity<Object>> getUser(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        return oauth2AuthorizerService.handleCommonAuthorizationErrors(
            oauth2AuthorizerService.extractTokenPair(token)
                .filter(pair -> Scope.grantsAny(pair.getScope(), Scope.USER, Scope.BOT))
                .switchIfEmpty(Mono.error(MissingRequiredPermissionException::new))
                .flatMap(unused -> userRepository.findById(userId))
                .switchIfEmpty(Mono.error(UserDoesNotExistException::new))
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
        ).onErrorReturn(UserDoesNotExistException.class, ResponseEntity.notFound().build());
    }
}
