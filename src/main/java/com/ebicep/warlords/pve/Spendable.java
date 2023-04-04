package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public interface Spendable {

    String getName();

    ChatColor getChatColor();

    ItemStack getItem();

    void addToPlayer(DatabasePlayer databasePlayer, long amount);

    Long getFromPlayer(DatabasePlayer databasePlayer);

    default void subtractFromPlayer(DatabasePlayer databasePlayer, long amount) {
        addToPlayer(databasePlayer, -amount);
    }

    default String getCostColoredName(long cost) {
        return getChatColor().toString() + NumberFormat.addCommas(cost) + " " + getName() + (cost == 1 || !pluralIncludeS() ? "" : "s");
    }

    default boolean pluralIncludeS() {
        return true;
    }


}
