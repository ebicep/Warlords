package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.AddCurrencyEvent;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.events.player.SupplyDropCallEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.events.StarPieceSynthesizedEvent;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HonorificListener implements Listener {

    @EventHandler
    public void onFirstLoad(DatabasePlayerFirstLoadEvent event) {
        if (!HonorificManager.honorificsEnabled()) {
            return;
        }
        HonorificManager.forceChallengeRefresh(event.getDatabasePlayer(), event.getPlayer());
    }

    @EventHandler
    public void onStarPieceSynthesized(StarPieceSynthesizedEvent event) {
        if (!HonorificManager.honorificsEnabled()) {
            return;
        }
        HonorificManager.recordStarPieceSynthesis(event.getUUID(), 1);
    }

    @EventHandler
    public void onSupplyDropCall(SupplyDropCallEvent event) {
        if (!HonorificManager.honorificsEnabled()) {
            return;
        }
        HonorificManager.recordSupplyDrops(event.getUUID(), event.getAmount());
    }

    @EventHandler
    public void onCurrencyChanged(AddCurrencyEvent event) {
        if (!HonorificManager.honorificsEnabled()) {
            return;
        }
        if (event.getAmount() >= 0 || !Currencies.STAR_PIECES.contains(event.getCurrency())) {
            return;
        }
        DatabasePlayer databasePlayer = HonorificManager.findDatabasePlayer(event.getDatabasePlayerPvE());
        if (databasePlayer == null || !DatabaseManager.inCache(databasePlayer.getUuid(), PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificManager.recordStarPiecesUsed(databasePlayer.getUuid(), -event.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onGameWin(WarlordsGameTriggerWinEvent event) {
        if (!HonorificManager.honorificsEnabled()) {
            return;
        }
        if (event.getGame().getAddons().contains(GameAddon.CUSTOM_GAME)) {
            return;
        }
        Team winner = event.getDeclaredWinner();
        PveOption pveOption = event.getGame().getOption(PveOption.class).stream().findFirst().orElse(null);
        event.getGame().warlordsPlayers().forEach(warlordsPlayer -> {
            HonorificManager.recordSingleGameDamage(
                    warlordsPlayer.getUuid(),
                    warlordsPlayer.getMinuteStats().total().getDamage()
            );
            if (pveOption != null) {
                long coins = Currencies.getCoinGainFromGameStats(warlordsPlayer, pveOption, false).getTotalCoinsGained();
                HonorificManager.recordSingleGameCoins(warlordsPlayer.getUuid(), coins);
            }
            if (winner != null && warlordsPlayer.getTeam() == winner && partyContainsPlikie(warlordsPlayer)) {
                HonorificManager.recordWinWithPlikie(warlordsPlayer.getUuid());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(WarlordsDeathEvent event) {
        if (!HonorificManager.honorificsEnabled()) {
            return;
        }
        WarlordsEntity victim = event.getWarlordsEntity();
        if (!(victim.getEntity() instanceof Player) || victim.getGame().getAddons().contains(GameAddon.CUSTOM_GAME)) {
            return;
        }
        if (!isOwnDamageDeath(event)) {
            return;
        }
        Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> {
            if (victim.isDead()) {
                HonorificManager.recordOwnDamageDeath(victim.getUuid());
            }
        });
    }

    private static boolean isOwnDamageDeath(WarlordsDeathEvent event) {
        if (event.getKiller() != event.getWarlordsEntity()) {
            return false;
        }
        Title title = event.getDeathInfo().title();
        if (title == null || title.subtitle() == null) {
            return true;
        }
        String subtitle = PlainTextComponentSerializer.plainText().serialize(title.subtitle());
        return !subtitle.contains("fall damage");
    }

    private static boolean partyContainsPlikie(WarlordsPlayer player) {
        Pair<Party, PartyPlayer> party = PartyManager.getPartyAndPartyPlayerFromAny(player.getUuid());
        if (party == null) {
            return false;
        }
        for (PartyPlayer member : party.getA().getPartyPlayers()) {
            if (member.getUUID().equals(player.getUuid())) {
                continue;
            }
            Player online = Bukkit.getPlayer(member.getUUID());
            String name = online != null ? online.getName() : Bukkit.getOfflinePlayer(member.getUUID()).getName();
            if (name != null && name.equalsIgnoreCase("Plikie")) {
                return true;
            }
        }
        return false;
    }
}
