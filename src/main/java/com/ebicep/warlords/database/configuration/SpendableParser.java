package com.ebicep.warlords.database.configuration;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.ExpSpendable;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.util.chat.ChatChannels;

import javax.annotation.Nonnull;

public class SpendableParser {

    public static Spendable parse(@Nonnull String s) {
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
        for (SpendableRandomNewItem value : SpendableRandomNewItem.VALUES) {
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
        throw new IllegalArgumentException("Could not convert " + s + " to Spendable");
    }

}
