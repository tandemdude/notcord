package io.github.tandemdude.notcord.authorizer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("email")
public class EmailConfig {
    private String address;
}
