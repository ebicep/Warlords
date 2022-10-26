package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogDailyCoinBonus;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GuildListener implements Listener {

    @EventHandler
    public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
        Player player = event.getPlayer();
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
        if (guildPlayerPair != null) {
            Guild guild = guildPlayerPair.getA();
            for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                if (upgrade.getUpgrade() == GuildUpgradesPermanent.DAILY_PLAYER_COIN_BONUS) {
                    GuildPlayer guildPlayer = guildPlayerPair.getB();
                    if (!guildPlayer.getJoinDate().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
                        return;
                    }
                    if (!guildPlayer.isDailyCoinBonusReceived()) {
                        guildPlayer.setDailyCoinBonusReceived(true);
                        long coins = (long) upgrade.getUpgrade().getValueFromTier(upgrade.getTier());
//                        guild.addCoins(coins);
                        guild.log(new GuildLogDailyCoinBonus(player.getUniqueId(), coins));
                        guild.sendGuildMessageToPlayer(
                                event.getPlayer(),
                                ChatColor.GRAY + "+" + ChatColor.GREEN + coins + " Player Coins " + ChatColor.GRAY + "from " +
                                        ChatColor.YELLOW + upgrade.getUpgrade().getName() +
                                        ChatColor.GRAY + " upgrade.",
                                true
                        );
                        guild.queueUpdate();
                        DatabasePlayer databasePlayer = event.getDatabasePlayer();
                        databasePlayer.getPveStats().addCurrency(Currencies.COIN, coins);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
                    return;
                }
            }

        }
    }

}
