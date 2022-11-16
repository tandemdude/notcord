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

package io.github.tandemdude.notcord.gateway.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.tandemdude.notcord.commons.enums.EventType;
import io.github.tandemdude.notcord.commons.models.KafkaMessage;
import io.github.tandemdude.notcord.gateway.conversion.converters.ChannelDataConverter;
import io.github.tandemdude.notcord.gateway.conversion.converters.GuildDataConverter;
import io.github.tandemdude.notcord.gateway.conversion.converters.MessageDataConverter;
import io.github.tandemdude.notcord.proto.Event;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class KafkaMessageConverter {
    // TODO - make the rest server publish events whenever applicable
    // TODO - implement the other converters and equivalent models in proto spec
    private static final Map<Long, BiFunction<Event.Builder, KafkaMessage, Event.Builder>> converterFunctions = Map.of(
        EventType.GUILD_CREATE, GuildDataConverter::convert,
        EventType.GUILD_DELETE, GuildDataConverter::convert,
        EventType.MESSAGE_CREATE, MessageDataConverter::convert,
        EventType.MESSAGE_UPDATE, MessageDataConverter::convert,
        EventType.MESSAGE_DELETE, MessageDataConverter::convert,
        EventType.CHANNEL_CREATE, ChannelDataConverter::convert,
        EventType.CHANNEL_UPDATE, ChannelDataConverter::convert,
        EventType.CHANNEL_DELETE, ChannelDataConverter::convert
    );

    public static <T> T mapNotNull(JsonNode node, Function<JsonNode, T> mapper, T defaultValue) {
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        return mapper.apply(node);
    }

    public static Event.Builder convertAndApply(Event.Builder builder, KafkaMessage message) {
        return converterFunctions.get(message.getType()) != null
            ? converterFunctions.get(message.getType()).apply(builder, message)
            : null;
    }
}
