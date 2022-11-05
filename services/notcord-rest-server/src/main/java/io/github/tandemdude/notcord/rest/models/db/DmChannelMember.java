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
@Table(name = "dm_channel_members", schema = "notcord")
public class DmChannelMember implements Persistable<String> {
    @Id
    private String rowId = null;
    private ChannelType channelType;
    private String channelId;
    private String userId;

    public DmChannelMember(ChannelType channelType, String channelId, String userId) {
        this.channelType = channelType;
        this.channelId = channelId;
        this.userId = userId;
    }


    @Override
    public String getId() {
        return this.rowId;
    }

    @Override
    public boolean isNew() {
        var isNew = this.rowId == null;
        this.rowId = isNew ? SnowflakeGenerator.newSnowflake() : this.rowId;
        return isNew;
    }
}
