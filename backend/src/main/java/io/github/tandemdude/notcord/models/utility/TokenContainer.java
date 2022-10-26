package io.github.tandemdude.notcord.models.utility;

import io.github.tandemdude.notcord.models.db.Oauth2TokenPair;
import lombok.Data;

@Data
public class TokenContainer<T> {
    private final Oauth2TokenPair tokenPair;
    private final T value;
}
