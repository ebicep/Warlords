package com.ebicep.warlords.database.configuration;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.ExpSpendable;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import javax.annotation.Nonnull;

@ReadingConverter
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
        for (SpendableRandomItem value : SpendableRandomItem.VALUES) {
            if (value.name().equalsIgnoreCase(s)) {
                return value;
            }
        }
        ChatChannels.sendDebugMessage((CommandIssuer) null, "Could not convert " + s + " to Spendable");
        return null;
    }

}
