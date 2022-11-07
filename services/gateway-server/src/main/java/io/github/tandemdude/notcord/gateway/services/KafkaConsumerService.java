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

import io.github.tandemdude.notcord.gateway.models.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class KafkaConsumerService implements CommandLineRunner {
    private final Flux<KafkaMessage> sharedFlux;

    public KafkaConsumerService(ReactiveKafkaConsumerTemplate<String, KafkaMessage> reactiveKafkaConsumerTemplate) {
        sharedFlux = reactiveKafkaConsumerTemplate.receiveAutoAck()
            .log()
            .map(ConsumerRecord::value)
            .share();
    }

    public Flux<KafkaMessage> consumeMessagesFor(String userId) {
        // TODO - check event is applicable for given userId
        return sharedFlux;
    }

    @Override
    public void run(String... args) {
        sharedFlux.subscribe();
    }
}
