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

package io.github.tandemdude.notcord.rest.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tandemdude.notcord.commons.models.KafkaMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Service
public class KafkaProducerService {
    private final ReactiveKafkaProducerTemplate<String, KafkaMessage> producerTemplate;
    private final String topic;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(
        ReactiveKafkaProducerTemplate<String, KafkaMessage> producerTemplate,
        @Value("${kafka.producer.topic}") String topic,
        ObjectMapper objectMapper
    ) {
        this.producerTemplate = producerTemplate;
        this.topic = topic;
        this.objectMapper = objectMapper;
    }

    protected JsonNode serializeObject(Object object) {
        return objectMapper.valueToTree(object);
    }

    public Mono<SenderResult<Void>> sendMessage(KafkaMessage partialMessage, Object messageData) {
        partialMessage.setData(serializeObject(messageData));
        return producerTemplate
            .send(topic, partialMessage)
            .doOnSuccess(result -> System.out.println("sent to " + topic + " " + result.recordMetadata().offset()));
    }
}
