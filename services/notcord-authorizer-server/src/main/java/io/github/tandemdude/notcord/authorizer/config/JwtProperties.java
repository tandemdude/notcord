package io.github.tandemdude.notcord.authorizer.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("jwt")
public class JwtProperties {
    @NotEmpty
    private String secret;
}
