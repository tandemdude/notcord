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

package io.github.tandemdude.notcord.gateway.config;

import io.github.tandemdude.notcord.gateway.models.KafkaMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ReceiverOptions<String, KafkaMessage> kafkaReceiverOptions(
        @Value("${kafka.consumer.topic}") String topic,
        KafkaProperties kafkaProperties
    ) {
        return ReceiverOptions.<String, KafkaMessage>create(kafkaProperties.buildConsumerProperties())
            .subscription(List.of(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, KafkaMessage> reactiveKafkaConsumerTemplate(
        ReceiverOptions<String, KafkaMessage> receiverOptions
    ) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
