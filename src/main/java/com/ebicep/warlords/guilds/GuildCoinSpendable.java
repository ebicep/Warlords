package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Guild spendable implementation for adding coins to guilds
 */
public class GuildCoinSpendable implements Spendable {

    @Override
    public String getName() {
        return "Guild Coin";
    }

    @Override
    public NamedTextColor getTextColor() {
        return NamedTextColor.GOLD;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.GOLD_INGOT);
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
        if (guildPlayerPair == null) {
            return;
        }
        guildPlayerPair.getA().addCurrentCoins(amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return 0L;
    }
}
