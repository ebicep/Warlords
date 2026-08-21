package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import javax.annotation.Nonnull;


public class NewItemAttributeConverter {

    @ReadingConverter
    public static class StringToNewItemAttributeConverter implements Converter<String, NewItemAttribute> {

        @Override
        public NewItemAttribute convert(@Nonnull String source) {
            return NewItemAttribute.getByDatabaseName(source);
        }

    }

    @WritingConverter
    public static class NewItemAttributeToStringConverter implements Converter<NewItemAttribute, String> {

        @Override
        public String convert(@Nonnull NewItemAttribute source) {
            return source.getDatabaseName();
        }

    }

}
