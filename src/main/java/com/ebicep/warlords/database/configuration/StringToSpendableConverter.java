package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.pve.Spendable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import javax.annotation.Nonnull;

@ReadingConverter
public class StringToSpendableConverter implements Converter<String, Spendable> {

    @Override
    public Spendable convert(@Nonnull String s) {
        return SpendableParser.parse(s);
    }

}
