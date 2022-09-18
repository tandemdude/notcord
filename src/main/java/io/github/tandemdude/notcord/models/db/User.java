package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;

@Data
@Table(name = "users", schema = "notcord")
public class User implements Persistable<String> {
    @Id
    private String id = null;
    private String username;
    // Discriminator????

    private String email;
    private String password;

    private Boolean emailVerified = false;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;  // Already hashed
    }

    @Override
    public boolean isNew() {
        var isNew = this.id == null;
        this.id = isNew ? SnowflakeGenerator.newSnowflake().toString() : this.id;
        return isNew;
    }
}
