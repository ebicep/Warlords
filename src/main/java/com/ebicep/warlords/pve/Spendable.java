package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

public interface Spendable {

    String getName();

    TextColor getTextColor();

    ItemStack getItem();

    void addToPlayer(DatabasePlayer databasePlayer, long amount);

    Long getFromPlayer(DatabasePlayer databasePlayer);

    default void subtractFromPlayer(DatabasePlayer databasePlayer, long amount) {
        addToPlayer(databasePlayer, -amount);
    }

    default Component getCostColoredName(long cost) {
        return Component.text(NumberFormat.addCommas(cost) + " " + getName() + (cost == 1 || !pluralIncludeS() ? "" : "s"), getTextColor());
    }

    default Component getCostColoredName(long cost, String prefix) {
        return Component.text(prefix + NumberFormat.addCommas(cost) + " " + getName() + (cost == 1 || !pluralIncludeS() ? "" : "s"), getTextColor());
    }

    default boolean pluralIncludeS() {
        return true;
    }


}
