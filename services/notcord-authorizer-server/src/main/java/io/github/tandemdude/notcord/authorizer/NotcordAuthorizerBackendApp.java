package io.github.tandemdude.notcord.authorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan("io.github.tandemdude.notcord.commons")
@ComponentScan("io.github.tandemdude.notcord.authorizer")
public class NotcordAuthorizerBackendApp {
    public static void main(String[] args) {
        SpringApplication.run(NotcordAuthorizerBackendApp.class);
    }
}
