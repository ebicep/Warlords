package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

@CommandAlias("pushcachebench")
@CommandPermission("group.adminisrator")
public class PushCacheBenchCommand extends BaseCommand {

    @Default
    @Description("Time DatabasePlayer.getKills vs treeWalkKills")
    public void bench(Player player, @Optional @Default("1000") @Conditions("limits:min=1,max=100000") Integer iterations) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        int cachedValue = databasePlayer.getKills();
        int treeWalkValue = databasePlayer.treeWalkKills();

        TimedLong cached = timeLong(iterations, () -> databasePlayer.getKills());
        TimedLong treeWalk = timeLong(iterations, () -> databasePlayer.treeWalkKills());

        log(player, header(databasePlayer.getName(), databasePlayer.pushedStats().isWarmed(), iterations));
        log(player, formatCompare("getKills", iterations, cached.nanos, treeWalk.nanos, cachedValue, treeWalkValue));
    }

    @Subcommand("honorifics")
    @Description("Time honorific PvE getters cached vs treeWalk")
    public void honorifics(Player player, @Optional @Default("1000") @Conditions("limits:min=1,max=100000") Integer iterations) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        Mob[] skeletonMobs = skeletonMobs();
        Mob[] ironGolemMobs = ironGolemMobs();

        Map<String, Long> cachedMap = pveStats.getMobKills();
        Map<String, Long> treeWalkMap = pveStats.treeWalkMobKills();
        pveStats.getTotalMobKills();
        mobKillCount(cachedMap, Mob.ONE_OF_NINE.name);
        totalMobKills(treeWalkMap);
        mobKillCount(treeWalkMap, Mob.ONE_OF_NINE.name);
        mobGroupKills(cachedMap, skeletonMobs);
        mobGroupKills(treeWalkMap, Mob.CHAMPION);

        TimedMap cachedMobKills = timeMap(iterations, pveStats::getMobKills);
        TimedMap treeWalkMobKills = timeMap(iterations, pveStats::treeWalkMobKills);

        TimedLong cachedTotal = timeLong(iterations, pveStats::getTotalMobKills);
        TimedLong treeWalkTotal = timeLong(iterations, () -> totalMobKills(pveStats.treeWalkMobKills()));

        TimedLong cachedOne = timeLong(iterations, () -> mobKillCount(pveStats.getMobKills(), Mob.ONE_OF_NINE.name));
        TimedLong treeWalkOne = timeLong(iterations, () -> mobKillCount(pveStats.treeWalkMobKills(), Mob.ONE_OF_NINE.name));
        TimedLong cachedLilium = timeLong(iterations, () -> mobKillCount(pveStats.getMobKills(), Mob.LILIUM.name));
        TimedLong treeWalkLilium = timeLong(iterations, () -> mobKillCount(pveStats.treeWalkMobKills(), Mob.LILIUM.name));
        TimedLong cachedGolem = timeLong(iterations, () -> mobGroupKills(pveStats.getMobKills(), ironGolemMobs));
        TimedLong treeWalkGolem = timeLong(iterations, () -> mobGroupKills(pveStats.treeWalkMobKills(), ironGolemMobs));

        TimedLong cachedSkeleton = timeLong(iterations, () -> mobGroupKills(pveStats.getMobKills(), skeletonMobs));
        TimedLong treeWalkSkeleton = timeLong(iterations, () -> mobGroupKills(pveStats.treeWalkMobKills(), skeletonMobs));
        TimedLong cachedChampion = timeLong(iterations, () -> mobGroupKills(pveStats.getMobKills(), Mob.CHAMPION));
        TimedLong treeWalkChampion = timeLong(iterations, () -> mobGroupKills(pveStats.treeWalkMobKills(), Mob.CHAMPION));

        log(player, header(databasePlayer.getName(), pveStats.pushedStats().isWarmed(), iterations));
        log(player, formatCompare(
                "getMobKills",
                iterations,
                cachedMobKills.nanos,
                treeWalkMobKills.nanos,
                cachedMobKills.value.size(),
                treeWalkMobKills.value.size(),
                StatPushUp.mapsEqual(cachedMobKills.value, treeWalkMobKills.value)
        ));
        log(player, formatCompare("getTotalMobKills", iterations, cachedTotal.nanos, treeWalkTotal.nanos, cachedTotal.value, treeWalkTotal.value));
        log(player, formatCompare("getMobKillCount(One of Nine)", iterations, cachedOne.nanos, treeWalkOne.nanos, cachedOne.value, treeWalkOne.value));
        log(player, formatCompare("getMobKillCount(Lilium)", iterations, cachedLilium.nanos, treeWalkLilium.nanos, cachedLilium.value, treeWalkLilium.value));
        log(player, formatCompare("ironGolemKills", iterations, cachedGolem.nanos, treeWalkGolem.nanos, cachedGolem.value, treeWalkGolem.value));
        log(player, formatCompare("skeletonKills", iterations, cachedSkeleton.nanos, treeWalkSkeleton.nanos, cachedSkeleton.value, treeWalkSkeleton.value));
        log(player, formatCompare("championKills", iterations, cachedChampion.nanos, treeWalkChampion.nanos, cachedChampion.value, treeWalkChampion.value));
    }

    private static TimedLong timeLong(int iterations, LongCall call) {
        long last = 0;
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            last = call.get();
        }
        return new TimedLong(System.nanoTime() - start, last);
    }

    private static TimedMap timeMap(int iterations, Supplier<Map<String, Long>> call) {
        Map<String, Long> last = Map.of();
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            last = call.get();
        }
        return new TimedMap(System.nanoTime() - start, last);
    }

    private static long totalMobKills(Map<String, Long> mobKills) {
        return mobKills.values().stream().mapToLong(value -> value == null ? 0 : value).sum();
    }

    private static long mobKillCount(Map<String, Long> mobKills, String mobName) {
        if (mobName == null || mobName.isEmpty()) {
            return 0;
        }
        Long value = mobKills.get(mobName);
        return value == null ? 0 : value;
    }

    private static Mob[] skeletonMobs() {
        return Arrays.stream(Mob.VALUES)
                .filter(mob -> mob.entityType == EntityType.SKELETON
                        || mob.entityType == EntityType.WITHER_SKELETON
                        || mob.entityType == EntityType.STRAY)
                .toArray(Mob[]::new);
    }

    private static Mob[] ironGolemMobs() {
        return Arrays.stream(Mob.VALUES)
                .filter(mob -> mob.entityType == EntityType.IRON_GOLEM)
                .toArray(Mob[]::new);
    }

    private static long mobGroupKills(Map<String, Long> mobKills, Mob[] mobs) {
        long sum = 0;
        for (Mob mob : mobs) {
            if (mob.name != null) {
                Long value = mobKills.get(mob.name);
                if (value != null) {
                    sum += value;
                }
            }
        }
        return sum;
    }

    private static String header(String name, boolean warmed, int iterations) {
        return "player=" + name + " warmed=" + warmed + " iterations=" + iterations;
    }

    private static String formatCompare(String label, int iterations, long cachedNanos, long treeWalkNanos, long cachedValue, long treeWalkValue) {
        return formatCompare(label, iterations, cachedNanos, treeWalkNanos, cachedValue, treeWalkValue, cachedValue == treeWalkValue);
    }

    private static String formatCompare(
            String label,
            int iterations,
            long cachedNanos,
            long treeWalkNanos,
            long cachedValue,
            long treeWalkValue,
            boolean match
    ) {
        double speedup = cachedNanos == 0 ? 0 : treeWalkNanos / (double) cachedNanos;
        return label
                + " " + (match ? "ok" : "MISMATCH")
                + "  cached " + formatTiming(iterations, cachedNanos)
                + "  treeWalk " + formatTiming(iterations, treeWalkNanos)
                + "  " + String.format("%.1fx", speedup)
                + "  values " + cachedValue + "/" + treeWalkValue;
    }

    private static String formatTiming(int iterations, long nanos) {
        return String.format("%.3fms (%dns/call)", nanos / 1_000_000.0, nanos / iterations);
    }

    private static void log(Player player, String message) {
        String line = "[pushcachebench] " + message;
        ChatUtils.MessageType.WARLORDS.sendMessage(line);
        ChatChannels.sendDebugMessage(player, line);
    }

    @FunctionalInterface
    private interface LongCall {
        long get();
    }

    private record TimedLong(long nanos, long value) {
    }

    private record TimedMap(long nanos, Map<String, Long> value) {
    }
}
