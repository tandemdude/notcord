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

import io.github.tandemdude.notcord.rest.models.db.Channel;
import io.github.tandemdude.notcord.rest.models.db.enums.ChannelType;
import lombok.Getter;

import java.util.List;

public class GroupDmChannelResponse extends ChannelResponse {
    @Getter
    private final List<String> memberIds;

    public GroupDmChannelResponse(String id, String name, List<String> memberIds) {
        super(id, ChannelType.GROUP_DM, null, name);
        this.memberIds = memberIds;
    }

    public static GroupDmChannelResponse from(Channel channel, List<String> memberIds) {
        return new GroupDmChannelResponse(
            channel.getId(), channel.getName(), memberIds
        );
    }
}
