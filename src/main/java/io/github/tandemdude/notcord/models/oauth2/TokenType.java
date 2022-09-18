package io.github.tandemdude.notcord.models.oauth2;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenType {
    BEARER("BEARER"),
    REFRESH("REFRESH");

    private final String value;

    TokenType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
