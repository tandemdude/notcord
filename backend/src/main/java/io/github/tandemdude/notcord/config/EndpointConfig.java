package io.github.tandemdude.notcord.config;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties("endpoints")
public class EndpointConfig {
    @URL
    @NotNull
    private String backend;
    @URL
    @NotNull
    private String frontend;

    public String cleanFrontendUrl() {
        return frontend.endsWith("/") ? frontend.substring(0, frontend.length() - 1) : frontend;
    }

    public String cleanBackendUrl() {
        return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
    }

    public String frontend404Page() {
        return cleanFrontendUrl() + "/404";
    }
}
