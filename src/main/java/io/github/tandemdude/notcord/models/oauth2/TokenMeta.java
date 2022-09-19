package io.github.tandemdude.notcord.models.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenMeta {
    private String subjectId;
    private TokenType type;
    private Set<Scope> scopes;
    private Long expires;

    public TokenMeta(String subjectId, TokenType type, Set<Scope> scopes, Long expires) {
        this.subjectId = subjectId;
        this.type = type;
        this.scopes = scopes;
        this.expires = expires;
    }

    public Map<String, Object> toMap() {
        return Map.of(
            "subjectId", this.subjectId,
            "type", this.type.getValue(),
            "scopes", String.join(",", this.scopes.stream().map(Scope::getValue).toList()),
            "expiresAt", this.expires
        );
    }
}
