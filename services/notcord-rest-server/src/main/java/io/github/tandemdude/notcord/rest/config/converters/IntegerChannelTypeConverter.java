package io.github.tandemdude.notcord.rest.config.converters;

import io.github.tandemdude.notcord.rest.models.db.enums.ChannelType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class IntegerChannelTypeConverter implements Converter<Integer, ChannelType> {
    @Override
    public ChannelType convert(Integer source) {
        return ChannelType.from(source);
    }
}
