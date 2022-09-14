package com.ebicep.warlords.guilds;

import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GuildListener implements Listener {

    @EventHandler
    public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(event.getPlayer());
        if (guildPlayerPair != null) {
            Guild guild = guildPlayerPair.getA();
            for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                if (upgrade.getUpgrade() == GuildUpgradesPermanent.DAILY_PLAYER_COIN_BONUS) {
                    GuildPlayer guildPlayer = guildPlayerPair.getB();
                    if (!guildPlayer.isCoinBonusReceived()) {
                        guildPlayer.setCoinBonusReceived(true);
                        event.getDatabasePlayer().getPveStats().addCurrency(Currencies.COIN, (long) upgrade.getUpgrade().getValueFromTier(upgrade.getTier()));
                    }
                    break;
                }
            }

        }
    }

}
