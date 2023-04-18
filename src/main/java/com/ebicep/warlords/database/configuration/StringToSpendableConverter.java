package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrops;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.Nonnull;

public class StringToSpendableConverter implements Converter<String, Spendable> {

    @Override
    public Spendable convert(@Nonnull String s) {
        for (Currencies value : Currencies.VALUES) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        for (MobDrops value : MobDrops.VALUES) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        return null;
    }

}
