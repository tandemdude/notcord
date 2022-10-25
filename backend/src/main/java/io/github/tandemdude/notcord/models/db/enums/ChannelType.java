package io.github.tandemdude.notcord.models.db.enums;


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
