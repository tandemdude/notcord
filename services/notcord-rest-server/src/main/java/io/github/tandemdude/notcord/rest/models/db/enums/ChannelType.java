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

package io.github.tandemdude.notcord.rest.models.db.enums;


import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ChannelType {
    DM(0),
    GROUP_DM(1),
    GUILD_TEXT(2);

    private static final Map<Integer, ChannelType> BY_VALUE = new HashMap<>();

    static {
        for (ChannelType e : values()) {
            BY_VALUE.put(e.value, e);
        }
    }

    private final int value;

    ChannelType(final int value) {
        this.value = value;
    }

    public static ChannelType from(int value) {
        return BY_VALUE.get(value);
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }
}
