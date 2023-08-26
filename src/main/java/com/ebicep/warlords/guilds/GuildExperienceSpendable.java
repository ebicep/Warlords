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
public class GuildExperienceSpendable implements Spendable {

    @Override
    public String getName() {
        return "Guild Experience";
    }

    @Override
    public NamedTextColor getTextColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.EXPERIENCE_BOTTLE);
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
        if (guildPlayerPair == null) {
            return;
        }
        guildPlayerPair.getA().addExperience(amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return 0L;
    }

    @Override
    public boolean pluralIncludeS() {
        return false;
    }
}
