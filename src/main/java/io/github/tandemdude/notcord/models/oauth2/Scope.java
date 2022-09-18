package io.github.tandemdude.notcord.models.oauth2;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Scope {
    IDENTITY("identity"),
    EMAIL("email"),
    GUILDS("guilds"),
    GUILDS_JOIN("guilds.join");

    private final String value;

    Scope(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static boolean contains(String value) {
        try {
            Scope.valueOf(value);
            return true;
        } catch (IllegalArgumentException | NullPointerException unused) {
            return false;
        }
    }
}
