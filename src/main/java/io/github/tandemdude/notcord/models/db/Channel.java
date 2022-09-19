package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

@Data
@Table(name = "channels", schema = "notcord")
public class Channel implements Persistable<String> {
    @Id
    private String id = null;
    private ChannelType type;
    private @Nullable String guildId;
    private String name;

    public Channel(ChannelType type, @Nullable String guildId, String name) {
        this.type = type;
        this.guildId = guildId;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake() : this.id;
        return isNew;
    }
}
