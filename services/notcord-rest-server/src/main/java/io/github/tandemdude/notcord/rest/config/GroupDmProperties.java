package io.github.tandemdude.notcord.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("group-dms")
public class GroupDmProperties {
    private int maxMembers;
    private int maxChannelsPerUser;
}
