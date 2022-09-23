package io.github.tandemdude.notcord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NotcordApp {
    public static void main(String[] args) {
        SpringApplication.run(NotcordApp.class);
    }
}
