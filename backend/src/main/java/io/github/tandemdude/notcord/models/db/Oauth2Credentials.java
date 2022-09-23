package io.github.tandemdude.notcord.models.db;

import io.github.tandemdude.notcord.utils.DefaultAvatarGenerator;
import io.github.tandemdude.notcord.utils.SnowflakeGenerator;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table(name = "oauth2_credentials", schema = "notcord")
public class Oauth2Credentials implements Persistable<String> {
    @Id
    private String clientId = null;
    private String clientSecret = null;
    private String appName;
    private String redirectUri;
    private String ownerId;
    private String defaultIconSvg = null;

    public Oauth2Credentials(String appName, String redirectUri, String ownerId) {
        this.appName = appName;
        this.redirectUri = redirectUri;
        this.ownerId = ownerId;
    }

    @Override
    public String getId() {
        return this.clientId;
    }

    @Override
    public boolean isNew() {
        var isNew = this.clientId == null;
        if (isNew) {
            this.clientId = SnowflakeGenerator.newSnowflake();
            this.clientSecret = UUID.randomUUID().toString().replace("-", "");
            this.defaultIconSvg = DefaultAvatarGenerator.generateDefaultAppIconSvg(this.appName);
        }
        return isNew;
    }
}
