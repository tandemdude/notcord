package io.github.tandemdude.notcord.rest.models.utility;

import lombok.Data;

@Data
public class TokenContainer<T> {
    private final Oauth2TokenPair tokenPair;
    private final T value;
}
