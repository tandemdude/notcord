package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.models.db.enums.ChannelType;
import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "dm_channel_members", schema = "notcord")
public class DmChannelMember implements Persistable<String> {
    @Id
    private String rowId = null;
    private ChannelType channelType;
    private String channelId;
    private String userId;

    public DmChannelMember(ChannelType channelType, String channelId, String userId) {
        this.channelType = channelType;
        this.channelId = channelId;
        this.userId = userId;
    }


    @Override
    public String getId() {
        return this.rowId;
    }

    @Override
    public boolean isNew() {
        var isNew = this.rowId == null;
        this.rowId = isNew ? SnowflakeGenerator.newSnowflake() : this.rowId;
        return isNew;
    }
}
