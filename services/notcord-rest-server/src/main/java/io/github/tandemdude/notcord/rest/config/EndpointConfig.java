package io.github.tandemdude.notcord.rest.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("endpoints")
public class EndpointConfig {
    @URL
    @NotNull
    private String frontend;
    @URL
    @NotNull
    private String rest;
    @URL
    @NotNull
    private String authorizer;

    public String cleanFrontendUrl() {
        return frontend.endsWith("/") ? frontend.substring(0, frontend.length() - 1) : frontend;
    }

    public String cleanRestUrl() {
        return rest.endsWith("/") ? rest.substring(0, rest.length() - 1) : rest;
    }

    public String cleanAuthorizerUrl() {
        return authorizer.endsWith("/") ? authorizer.substring(0, authorizer.length() - 1) : authorizer;
    }

    public String frontend404Page() {
        return cleanFrontendUrl() + "/404";
    }
}
