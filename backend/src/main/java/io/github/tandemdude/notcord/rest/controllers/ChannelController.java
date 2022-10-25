package io.github.tandemdude.notcord.rest.controllers;

import io.github.tandemdude.notcord.models.responses.ChannelResponse;
import io.github.tandemdude.notcord.repositories.ChannelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {
    private final ChannelRepository channelRepository;

    public ChannelController(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @GetMapping("/{channelId:[1-9][0-9]+}")
    public Mono<ResponseEntity<ChannelResponse>> getChannel(@PathVariable String channelId) {
        // TODO - authorization
        return channelRepository.findById(channelId).map(ChannelResponse::from).map(ResponseEntity::ok).switchIfEmpty(
            Mono.just(ResponseEntity.notFound().build()));
    }

    //    @PatchMapping("/{channelId:[1-9][0-9]+}")
    //    public Mono<?> updateChannel(@PathVariable String channelId) {
    //
    //    }

    //    @DeleteMapping("/{channelId:[1-9][0-9]+}")
    //    public Mono<?> deleteChannel(@PathVariable String channelId) {
    //
    //    }
}
