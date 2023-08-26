package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GuildSpendable implements Spendable {

    COIN("Coin") {
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
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
            if (guildPlayerPair == null) {
                return 0L;
            }
            return guildPlayerPair.getA().getCurrentCoins();
        }
    },
    EXPERIENCE("Experience") {
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
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
            if (guildPlayerPair == null) {
                return 0L;
            }
            return guildPlayerPair.getA().getExperience(Timing.LIFETIME);
        }

        @Override
        public boolean pluralIncludeS() {
            return false;
        }
    },

    ;

    public final String name;

    GuildSpendable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return "Guild " + name;
    }


}
