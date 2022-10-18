package io.github.tandemdude.notcord.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;

import java.util.List;

@Configuration
public class R2dbcConfig {
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(
        ConnectionFactory connectionFactory, List<Converter<?, ?>> converters
    ) {
        return R2dbcCustomConversions.of(DialectResolver.getDialect(connectionFactory), converters);
    }
}
