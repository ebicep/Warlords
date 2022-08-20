package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("mystats")
@Conditions("database:player")
public class MyStatsCommand extends BaseCommand {

    @Default
    public void myStats(Player player, @Optional Specializations spec) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        StringBuilder stats = new StringBuilder(ChatColor.GOLD + "Your Stats");
        if (spec == null) {
            HashMap<WeaponsPvE, AtomicInteger> rarityWins = new HashMap<>();
            for (MasterworksFairEntry masterworksFairEntry : databasePlayerPvE.getMasterworksFairEntries()) {
                if (masterworksFairEntry.getPlacement() == 1) {
                    rarityWins.computeIfAbsent(masterworksFairEntry.getRarity(), v -> new AtomicInteger(0)).incrementAndGet();
                }
            }

            stats.append(ChatColor.GREEN).append("\n Games Played: ").append(databasePlayerPvE.getPlays());
            stats.append(ChatColor.GREEN).append("\n Wins: ").append(databasePlayerPvE.getWins());
            stats.append(ChatColor.GREEN).append("\n Waves Cleared: ").append(databasePlayerPvE.getTotalWavesCleared());
            stats.append(ChatColor.GREEN).append("\n DHP: ").append(databasePlayerPvE.getDHP());
            stats.append(ChatColor.GREEN).append("\n  Damage: ").append(databasePlayerPvE.getDamage());
            stats.append(ChatColor.GREEN).append("\n  Healing: ").append(databasePlayerPvE.getHealing());
            stats.append(ChatColor.GREEN).append("\n  Absorbed: ").append(databasePlayerPvE.getAbsorbed());
            stats.append(ChatColor.GREEN).append("\n Masterworks Fair Wins: ").append(rarityWins.values().stream()
                    .mapToInt(AtomicInteger::intValue)
                    .sum());
            for (WeaponsPvE value : WeaponsPvE.values()) {
                if (value.getPlayerEntries != null) {
                    stats.append(ChatColor.GREEN).append("\n  Masterworks Fair ").append(value.name).append(" Wins: ").append(rarityWins.getOrDefault(value, new AtomicInteger(0)).intValue());
                }
            }
        } else {
            PvEDatabaseStatInformation specStats = databasePlayerPvE.getSpec(databasePlayer.getLastSpec());
            stats.append(ChatColor.GREEN).append("\n Games Played: ").append(specStats.getPlays());
            stats.append(ChatColor.GREEN).append("\n Wins: ").append(specStats.getWins());
            stats.append(ChatColor.GREEN).append("\n Waves Cleared: ").append(specStats.getTotalWavesCleared());
            stats.append(ChatColor.GREEN).append("\n DHP: ").append(specStats.getDHP());
            stats.append(ChatColor.GREEN).append("\n  Damage: ").append(specStats.getDamage());
            stats.append(ChatColor.GREEN).append("\n  Healing: ").append(specStats.getHealing());
            stats.append(ChatColor.GREEN).append("\n  Absorbed: ").append(specStats.getAbsorbed());
            stats.append(ChatColor.GREEN).append("\n Kills: ").append(specStats.getKills());
            stats.append(ChatColor.GREEN).append("\n Assists: ").append(specStats.getAssists());
        }

        player.sendMessage(stats.toString());
    }


}
