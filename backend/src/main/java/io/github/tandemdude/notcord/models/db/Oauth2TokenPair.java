package io.github.tandemdude.notcord.models.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.UUID;

@Data
@Table(name = "oauth2_tokens", schema = "notcord")
public class Oauth2TokenPair implements Persistable<String> {
    @Id
    private String id = null;
    private String type;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    private long expiresIn;
    private String userId;
    private long scope;
    // The client ID that the tokens are linked to. Will be null if tokens were generated for the frontend
    @Nullable
    private String clientId;

    public Oauth2TokenPair(
        String type,
        String accessToken,
        String refreshToken,
        Instant expiresAt,
        long expiresIn,
        String userId,
        long scope,
        String clientId
    ) {
        this.type = type;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.scope = scope;
        this.clientId = clientId;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        if (isNew) {
            this.id = UUID.randomUUID().toString().replace("-", "");
        }
        return isNew;
    }
}
