package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.models.requests.MessageCreateRequestBody;
import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "messages", schema = "notcord")
public class Message implements Persistable<String> {
    @Id
    private String id = null;
    private String channelId;
    private String authorId;
    private String guildId;
    private String content;
    private Integer nonce;

    public Message(String channelId, String authorId, String guildId, String content, Integer nonce) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.guildId = guildId;
        this.content = content;
        this.nonce = nonce;
    }

    public Message(String channelId, String authorId, String guildId, MessageCreateRequestBody body) {
        this(channelId, authorId, guildId, body.getContent(), body.getNonce());
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake() : this.id;
        return isNew;
    }
}
