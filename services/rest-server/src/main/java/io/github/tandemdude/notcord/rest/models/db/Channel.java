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
import io.github.tandemdude.notcord.rest.models.db.enums.ChannelType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "channels", schema = "notcord")
public class Channel implements Persistable<String> {
    @Id
    private String id = null;
    private ChannelType type;
    private String name;
    private String guildId;
    private Integer memberLimit;
    private String ownerId;

    public Channel(ChannelType type, String name, String guildId, Integer memberLimit, String ownerId) {
        this.type = type;
        this.name = name;
        this.guildId = guildId;
        this.memberLimit = memberLimit;
        this.ownerId = ownerId;
    }

    public static Channel newGuildChannel(ChannelType type, String name, String guildId) {
        return new Channel(type, name, guildId, null, null);
    }

    public static Channel newDmChannel() {
        return new Channel(ChannelType.DM, null, null, null, null);
    }

    public static Channel newGroupDmChannel(String name, int memberLimit, String ownerId) {
        return new Channel(ChannelType.GROUP_DM, name, null, memberLimit, ownerId);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake() : this.id;
        return isNew;
    }

    public boolean isDm() {
        return this.type.equals(ChannelType.DM);
    }

    public boolean isGroupDm() {
        return this.type.equals(ChannelType.GROUP_DM);
    }
}
