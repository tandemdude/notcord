package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Data
@Table(name = "guilds", schema = "notcord")
public class Guild implements Persistable<String> {
    @Id
    private String id = null;
    private String ownerId;
    private String name;

    public Guild(BigInteger ownerId, String name) {
        this.ownerId = ownerId.toString();
        this.name = name;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake().toString() : this.id;
        return isNew;
    }
}
