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

package io.github.tandemdude.notcord.rest.models.db;

import io.github.tandemdude.notcord.commons.utility.SnowflakeGenerator;
import io.github.tandemdude.notcord.rest.models.requests.MessageCreateRequestBody;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "messages", schema = "notcord")
public class Message implements Persistable<String> {
    @Id
    private String id = null;
    private String channelId;
    private String authorId;
    private String guildId;
    private String content;
    private Integer nonce;

    public Message(String channelId, String authorId, String guildId, String content, Integer nonce) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.guildId = guildId;
        this.content = content;
        this.nonce = nonce;
    }

    public Message(String channelId, String authorId, String guildId, MessageCreateRequestBody body) {
        this(channelId, authorId, guildId, body.getContent(), body.getNonce());
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake() : this.id;
        return isNew;
    }
}
