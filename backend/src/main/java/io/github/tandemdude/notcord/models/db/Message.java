package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "messages", schema = "notcord")
public class Message implements Persistable<String> {
    @Id
    private String id = null;
    private String channelId;
    private String authorId;
    private String guildId;
    private String content;

    public Message(String channelId, String authorId, String guildId, String content) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.guildId = guildId;
        this.content = content;
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake() : this.id;
        return isNew;
    }
}
