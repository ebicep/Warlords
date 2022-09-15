package com.ebicep.warlords.guilds;

import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GuildListener implements Listener {

    @EventHandler
    public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(event.getPlayer());
        if (guildPlayerPair != null) {
            Guild guild = guildPlayerPair.getA();
            for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                if (upgrade.getUpgrade() == GuildUpgradesPermanent.DAILY_PLAYER_COIN_BONUS) {
                    GuildPlayer guildPlayer = guildPlayerPair.getB();
                    if (!guildPlayer.getJoinDate().isBefore(Instant.now().minus(1, ChronoUnit.DAYS))) {
                        return;
                    }
                    if (!guildPlayer.isCoinBonusReceived()) {
                        guildPlayer.setCoinBonusReceived(true);
                        guild.addCoins((long) upgrade.getUpgrade().getValueFromTier(upgrade.getTier()));
                        //event.getDatabasePlayer().getPveStats().addCurrency(Currencies.COIN, (long) upgrade.getUpgrade().getValueFromTier(upgrade.getTier()));
                    }
                    return;
                }
            }

        }
    }

}
