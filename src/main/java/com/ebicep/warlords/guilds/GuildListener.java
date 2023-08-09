package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogDailyCoinBonus;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
                    if (!guildPlayer.getJoinDate().isBefore(Instant.now().minus(2, ChronoUnit.DAYS))) {
                        return;
                    }
                    if (!guildPlayer.isDailyCoinBonusReceived()) {
                        guildPlayer.setDailyCoinBonusReceived(true);
                        long coins = (long) upgrade.getUpgrade().getValueFromTier(upgrade.getTier());
//                        guild.addCoins(coins);
                        guild.log(new GuildLogDailyCoinBonus(player.getUniqueId(), coins));
                        guild.sendGuildMessageToPlayer(
                                event.getPlayer(),
                                Component.text("+", NamedTextColor.GRAY)
                                         .append(Component.text(coins + " Player Coins ", NamedTextColor.GREEN))
                                         .append(Component.text("from "))
                                         .append(Component.text(upgrade.getUpgrade().getName(), NamedTextColor.YELLOW))
                                         .append(Component.text(" upgrade.")),
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
        if (guildPlayerPair == null) {
            return;
        }
        guildPlayerPair.getA().sendMOTD(player);
    }

}
