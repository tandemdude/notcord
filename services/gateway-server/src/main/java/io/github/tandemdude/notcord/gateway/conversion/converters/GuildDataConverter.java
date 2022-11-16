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

import io.github.tandemdude.notcord.commons.models.KafkaMessage;
import io.github.tandemdude.notcord.proto.Event;
import io.github.tandemdude.notcord.proto.GuildData;

public class GuildDataConverter {
    public static Event.Builder convert(Event.Builder builder, KafkaMessage message) {
        var data = message.getData();
        return builder.setGuild(
            GuildData.newBuilder()
                .setId(data.get("id").asText())
                .setOwnerId(data.get("ownerId").asText())
                .setName(data.get("name").asText())
                .build()
        );
    }
}
