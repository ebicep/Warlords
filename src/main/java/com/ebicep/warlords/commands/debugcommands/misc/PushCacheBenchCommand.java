package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("pushcachebench")
@CommandPermission("group.adminisrator")
public class PushCacheBenchCommand extends BaseCommand {

    @Default
    @Description("Time DatabasePlayer.getKills vs treeWalkKills")
    public void bench(Player player, @Optional @Default("1000") @Conditions("limits:min=1,max=100000") Integer iterations) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        int warmupKills = databasePlayer.getKills();
        databasePlayer.treeWalkKills();

        int lastCached = warmupKills;
        long cachedStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            lastCached = databasePlayer.getKills();
        }
        long cachedNanos = System.nanoTime() - cachedStart;

        int lastTreeWalk = lastCached;
        long treeWalkStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            lastTreeWalk = databasePlayer.treeWalkKills();
        }
        long treeWalkNanos = System.nanoTime() - treeWalkStart;

        log(player, "player=" + databasePlayer.getName()
                + " warmed=" + databasePlayer.pushedStats().isWarmed()
                + " cachedKills=" + lastCached
                + " treeWalkKills=" + lastTreeWalk
                + " iterations=" + iterations);
        log(player, formatLoop("getKills", iterations, cachedNanos));
        log(player, formatLoop("treeWalkKills", iterations, treeWalkNanos));
    }

    private static String formatLoop(String label, int iterations, long nanos) {
        double millis = nanos / 1_000_000.0;
        long avgNanos = nanos / iterations;
        return label + " total=" + nanos + "ns (" + String.format("%.3f", millis) + "ms) avg=" + avgNanos + "ns/call";
    }

    private static void log(Player player, String message) {
        String line = "[pushcachebench] " + message;
        ChatUtils.MessageType.WARLORDS.sendMessage(line);
        ChatChannels.sendDebugMessage(player, line);
    }
}
