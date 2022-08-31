package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("mystats")
@Conditions("database:player")
public class MyStatsCommand extends BaseCommand {

    @Default
    public void myStats(Player player, @Optional Specializations spec) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        StringBuilder stats;
        if (spec == null) {
            stats = new StringBuilder(ChatColor.GOLD + "Your Stats");
            HashMap<WeaponsPvE, AtomicInteger> rarityWins = new HashMap<>();
            for (MasterworksFairEntry masterworksFairEntry : databasePlayerPvE.getMasterworksFairEntries()) {
                if (masterworksFairEntry.getPlacement() == 1) {
                    rarityWins.computeIfAbsent(masterworksFairEntry.getRarity(), v -> new AtomicInteger(0)).incrementAndGet();
                }
            }

            stats.append("\n").append(ChatColor.GREEN).append("Games Played: ").append(databasePlayerPvE.getPlays());
            stats.append("\n").append(ChatColor.GREEN).append("Wins: ").append(databasePlayerPvE.getWins());
            stats.append("\n").append(ChatColor.GREEN).append("Waves Cleared: ").append(databasePlayerPvE.getTotalWavesCleared());
            stats.append("\n").append(ChatColor.GREEN).append("DHP: ").append(databasePlayerPvE.getDHP());
            stats.append("\n").append(ChatColor.GREEN).append("Damage: ").append(databasePlayerPvE.getDamage());
            stats.append("\n").append(ChatColor.GREEN).append("Healing: ").append(databasePlayerPvE.getHealing());
            stats.append("\n").append(ChatColor.GREEN).append("Absorbed: ").append(databasePlayerPvE.getAbsorbed());
            stats.append("\n").append(ChatColor.GREEN).append("Masterworks Fair Wins: ").append(rarityWins.values().stream()
                    .mapToInt(AtomicInteger::intValue)
                    .sum());
            for (WeaponsPvE value : WeaponsPvE.VALUES) {
                if (value.getPlayerEntries != null) {
                    stats.append("\n").append(ChatColor.GREEN).append("Masterworks Fair ").append(value.name).append(" Wins: ").append(rarityWins.getOrDefault(value, new AtomicInteger(0)).intValue());
                }
            }
        } else {
            stats = new StringBuilder(ChatColor.GOLD + "Your Stats (" + spec.name + ")");

            PvEDatabaseStatInformation specStats = databasePlayerPvE.getSpec(spec);
            stats.append("\n").append(ChatColor.GREEN).append("Games Played: ").append(specStats.getPlays());
            stats.append("\n").append(ChatColor.GREEN).append("Wins: ").append(specStats.getWins());
            stats.append("\n").append(ChatColor.GREEN).append("Waves Cleared: ").append(specStats.getTotalWavesCleared());
            stats.append("\n").append(ChatColor.GREEN).append("DHP: ").append(specStats.getDHP());
            stats.append("\n").append(ChatColor.GREEN).append("Damage: ").append(specStats.getDamage());
            stats.append("\n").append(ChatColor.GREEN).append("Healing: ").append(specStats.getHealing());
            stats.append("\n").append(ChatColor.GREEN).append("Absorbed: ").append(specStats.getAbsorbed());
            stats.append("\n").append(ChatColor.GREEN).append("Kills: ").append(specStats.getKills());
            stats.append("\n").append(ChatColor.GREEN).append("Assists: ").append(specStats.getAssists());
        }

        ChatUtils.sendMessageToPlayer(player, stats.toString(), ChatColor.GREEN, true);
    }


}
