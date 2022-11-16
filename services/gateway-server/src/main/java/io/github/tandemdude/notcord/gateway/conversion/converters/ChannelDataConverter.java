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

package io.github.tandemdude.notcord.gateway.conversion.converters;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.tandemdude.notcord.commons.models.KafkaMessage;
import io.github.tandemdude.notcord.gateway.conversion.KafkaMessageConverter;
import io.github.tandemdude.notcord.proto.ChannelData;
import io.github.tandemdude.notcord.proto.Event;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChannelDataConverter {
    @SuppressWarnings("unchecked")
    public static Event.Builder convert(Event.Builder builder, KafkaMessage message) {
        var data = message.getData();
        return builder.setChannel(
            ChannelData.newBuilder()
                .setId(data.get("id").asText())
                .setType(data.get("type").asInt())
                .setName(KafkaMessageConverter.mapNotNull(data.get("name"), JsonNode::asText, ""))
                .setGuildId(KafkaMessageConverter.mapNotNull(data.get("guildId"), JsonNode::asText, ""))
                .setOwnerId(KafkaMessageConverter.mapNotNull(data.get("ownerId"), JsonNode::asText, ""))
                .addAllMemberIds(KafkaMessageConverter.mapNotNull(
                    data.get("memberIds"),
                    node -> (Iterable<String>) StreamSupport
                        .stream(node.spliterator(), false)
                        .map(JsonNode::asText)
                        .iterator(),
                    (Iterable<String>) Stream.empty().iterator()
                ))
                .build()
        );
    }
}
