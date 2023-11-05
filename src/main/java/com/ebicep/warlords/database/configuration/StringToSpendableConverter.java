package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.ExpSpendable;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrop;
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
        for (MobDrop value : MobDrop.VALUES) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        for (GuildSpendable value : GuildSpendable.VALUES) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        for (ExpSpendable value : ExpSpendable.VALUES) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        return null;
    }

}
