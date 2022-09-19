package io.github.tandemdude.notcord.config.converters;

import io.github.tandemdude.notcord.models.db.ChannelType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChannelTypeIntegerConverter implements Converter<ChannelType, Integer> {
    @Override
    public Integer convert(ChannelType source) {
        return source.getValue();
    }
}
