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

package io.github.tandemdude.notcord.rest.models.responses;

import io.github.tandemdude.notcord.rest.models.db.Message;
import lombok.Data;

@Data
public class MessageResponse {
    private final String id;
    private final String channelId;
    private final String authorId;
    private final String guildId;
    private final String content;
    private final Integer nonce;

    public static MessageResponse from(Message message) {
        return new MessageResponse(
            message.getId(),
            message.getChannelId(),
            message.getAuthorId(),
            message.getGuildId(),
            message.getContent(),
            message.getNonce()
        );
    }
}
