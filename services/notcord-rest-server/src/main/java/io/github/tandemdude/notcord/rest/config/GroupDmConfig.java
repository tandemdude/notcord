package io.github.tandemdude.notcord.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("group-dms")
public class GroupDmConfig {
    private int maxMembers;
    private int maxChannelsPerUser;
}
