package io.github.tandemdude.notcord.authorizer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfig implements WebFluxConfigurer {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
            .pathMatchers("/**")
            .permitAll()
            .anyExchange()
            .authenticated()
            .and()
            .formLogin()
            .disable()
            .csrf()
            .disable()
            .logout()
            .disable()
            .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:[*]/")
            .allowedMethods("POST")
            .maxAge(3600);
    }
}
