package io.github.tandemdude.notcord.authorizer.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("endpoints")
public class EndpointProperties {
    @URL
    @NotEmpty
    private String frontend;
    @URL
    @NotEmpty
    private String rest;
    @URL
    @NotEmpty
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
