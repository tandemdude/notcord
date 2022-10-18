package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.responses.UserResponse;
import io.github.tandemdude.notcord.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId:[1-9][0-9]+}")
    public Mono<ResponseEntity<UserResponse>> getUser(@PathVariable String userId) {
        // TODO - authorization
        return userRepository.findById(userId).map(UserResponse::from).map(ResponseEntity::ok).switchIfEmpty(Mono.just(
            ResponseEntity.notFound().build()));
    }
}
