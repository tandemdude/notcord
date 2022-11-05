package io.github.tandemdude.notcord.authorizer.config;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("email")
public class EmailProperties {
    @Email
    private String address;
}
