/*
 * Copyright 2022 tandemdude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tandemdude.notcord.gateway.services;

import io.github.tandemdude.notcord.commons.enums.Scope;
import io.github.tandemdude.notcord.commons.models.Oauth2TokenInfo;
import io.github.tandemdude.notcord.gateway.config.EndpointProperties;
import io.github.tandemdude.notcord.gateway.conversion.KafkaMessageConverter;
import io.github.tandemdude.notcord.proto.Event;
import io.github.tandemdude.notcord.proto.Identity;
import io.github.tandemdude.notcord.proto.ReactorGatewayServiceGrpc;
import io.github.tandemdude.notcord.proto.SessionDetails;
import io.grpc.Status;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GrpcHandlerService extends ReactorGatewayServiceGrpc.GatewayServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcHandlerService.class);
    private static final WebClient webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient
            .create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)))
        .build();

    private final EndpointProperties endpointProperties;
    private final KafkaConsumerService kafkaConsumerService;
    private final Map<String, String> sessionIds;

    public GrpcHandlerService(EndpointProperties endpointProperties, KafkaConsumerService kafkaConsumerService) {
        this.endpointProperties = endpointProperties;
        this.kafkaConsumerService = kafkaConsumerService;
        this.sessionIds = new ConcurrentHashMap<>();
    }

    @Override
    public Mono<SessionDetails> createSession(Mono<Identity> identity) {
        return identity
            .doOnNext(pl -> LOGGER.info("New session create request " + pl))
            .flatMap(pl -> webClient.post()
                .uri(endpointProperties.cleanAuthorizerUrl(), builder -> builder
                    .pathSegment("oauth", "token_info")
                    .queryParam("token", pl.getToken().strip())
                    .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.statusCode().is2xxSuccessful()
                    ? response.bodyToMono(Oauth2TokenInfo.class)
                    : Mono.error(Status.UNAUTHENTICATED::asException)))
            .filter(tokenInfo -> tokenInfo.getExpiresAt().isAfter(Instant.now()))
            .switchIfEmpty(Mono.error(Status.UNAUTHENTICATED::asException))
            .filter(tokenInfo -> Scope.grantsAny(tokenInfo.getScope(), Scope.USER, Scope.BOT))
            .switchIfEmpty(Mono.error(Status.PERMISSION_DENIED::asException))
            .map(tokenInfo -> SessionDetails
                .newBuilder()
                .setSessionId("foobar")
                .setUserId(tokenInfo.getUserId())
                .build())
            .doOnNext(details -> sessionIds.put(details.getSessionId(), details.getUserId()));
    }

    @Override
    public Flux<Event> consumeEvents(Mono<SessionDetails> sessionDetails) {
        return sessionDetails
            .filter(details -> sessionIds.containsKey(details.getSessionId()))
            .switchIfEmpty(Mono.error(Status.UNAUTHENTICATED::asException))
            .flatMapMany(details -> kafkaConsumerService.consumeMessagesFor(sessionIds.get(details.getSessionId())))
            // TODO - move conversion logic elsewhere
            // TODO - decide on proper conversion logic and event/kafkamessage format
            .mapNotNull(kafkaMessage -> KafkaMessageConverter
                .convertAndApply(Event.newBuilder().setType(kafkaMessage.getType()), kafkaMessage))
            .map(Event.Builder::build);
    }
}
