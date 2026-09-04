package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.featureflags.FeatureFlags;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HonorificManager {

    private static final Set<String> SKELETON_MOB_NAMES = mobDisplayNames(Arrays.stream(Mob.VALUES)
            .filter(mob -> mob.entityType == EntityType.SKELETON
                    || mob.entityType == EntityType.WITHER_SKELETON
                    || mob.entityType == EntityType.STRAY)
            .toArray(Mob[]::new));
    private static final Set<String> CHAMPION_MOB_NAMES = mobDisplayNames(Mob.CHAMPION);
    private static final Set<String> IRON_GOLEM_MOB_NAMES = mobDisplayNames(Arrays.stream(Mob.VALUES)
            .filter(mob -> mob.entityType == EntityType.IRON_GOLEM)
            .toArray(Mob[]::new));
    private static final long REFRESH_INTERVAL_MILLIS = 15_000;
    private static final ConcurrentHashMap<UUID, Long> LAST_CHALLENGE_REFRESH = new ConcurrentHashMap<>();
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean();

    private HonorificManager() {
    }

    public static void removeCachedPlayer(UUID uuid) {
        LAST_CHALLENGE_REFRESH.remove(uuid);
    }

    public static void init() {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(Warlords.getInstance(), HonorificManager::init);
            return;
        }
        if (!INITIALIZED.compareAndSet(false, true)) {
            return;
        }
        Bukkit.getPluginManager().registerEvents(new HonorificListener(), Warlords.getInstance());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!honorificsEnabled()) {
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
                        continue;
                    }
                    LAST_CHALLENGE_REFRESH.put(uuid, System.currentTimeMillis());
                    DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
                    if (refreshChallengeUnlocks(databasePlayer, player)) {
                        queueSave(uuid);
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20 * 10, 20 * 10);
    }

    public static HonorificProfile getProfile(UUID uuid) {
        init();
        refreshChallengesIfDue(uuid);
        return DatabaseManager.getPlayer(uuid).getHonorifics();
    }

    public static HonorificProfile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    public static HonorificProfile getProfile(DatabasePlayer databasePlayer) {
        return databasePlayer.getHonorifics();
    }

    public static Component getHonorificComponent(UUID uuid) {
        HonorificProfile profile = getProfile(uuid);
        Honorific honorific = profile.getEquippedHonorific();
        if (honorific == null) {
            return Component.empty();
        }
        Component title = profile.getSelectedFont().createComponent(
                honorific.getDisplayName(),
                profile.getSelectedColor().getTextColor()
        );
        return Component.text("{", NamedTextColor.DARK_GRAY)
                .append(title)
                .append(Component.text("} ", NamedTextColor.DARK_GRAY));
    }

    public static void forceChallengeRefresh(DatabasePlayer databasePlayer, @Nullable Player player) {
        LAST_CHALLENGE_REFRESH.put(databasePlayer.getUuid(), System.currentTimeMillis());
        if (refreshChallengeUnlocks(databasePlayer, player)) {
            queueSave(databasePlayer.getUuid());
        }
    }

    public static boolean refreshChallengeUnlocks(DatabasePlayer databasePlayer, @Nullable Player player) {
        if (!honorificsEnabled()) {
            return false;
        }
        HonorificProfile profile = databasePlayer.getHonorifics();
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        profile.setMinimumItemRerolls(pveStats.getNewItemsManager().getItemInventory().stream()
                .mapToLong(item -> item.getRerollCostsHistory().size()).sum());
        profile.setMinimumSupplyDropsRolled(pveStats.getSupplyDropEntries().size());

        boolean changed = false;
        for (Honorific honorific : Honorific.VALUES) {
            if (honorific.isPurchasable() || profile.isUnlocked(honorific) || !meetsRequirement(honorific, databasePlayer, profile)) {
                continue;
            }
            if (profile.unlock(honorific)) {
                changed = true;
                notifyUnlock(player, honorific);
            }
        }
        if (changed) {
            refreshDisplays(player);
        }
        return changed;
    }

    public static String getProgressText(Honorific honorific, DatabasePlayer databasePlayer) {
        HonorificProfile profile = getProfile(databasePlayer);
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        return switch (honorific) {
            case THE_MIGHTY_ROLLER -> progress(profile.getItemRerolls(), 90);
            case SYNTHESIZER -> progress(profile.getStarPiecesSynthesized(), 100);
            case EXTREMA -> {
                long fastest = getFastestExtremeTicks(pveStats);
                yield fastest == 0 ? "No completed Extreme run" : formatTicks(fastest) + " / 08:00";
            }
            case ONE_FOR_ALL -> progress(getMobKills(pveStats, Mob.ONE_OF_NINE), 1);
            case THE_HEART_THIEF -> progress(getMobKills(pveStats, Mob.LILIUM), 1);
            case BOUNDLESS -> progress(getHighestEndlessWave(pveStats), 200);
            case GOLEM -> progress(getIronGolemKills(pveStats), 50_000);
            case GOD_OF_WAR -> progress(getTotalMobKills(pveStats), 2_000_000);
            case EXPLORER -> progress(profile.getHighestAncientRenegadesFloor(), 30);
            case TREASURER -> progress(profile.getHighestAncientRenegadesFloor(), 100);
            case SLAUGHTERER -> formatTicks(getLongestOnslaughtTicks(pveStats)) + " / 180:00";
            case STAR_GUIDE -> progress(profile.getStarPiecesUsed(), 500);
            case SUPPLIER -> progress(profile.getSupplyDropsRolled(), 50_000);
            case PRESTIGIOUS -> progress(getHighestPrestige(databasePlayer), 30);
            case SKELETRON -> progress(getSkeletonKills(pveStats), 50_000);
            case CHAMPION -> progress(getChampionKills(pveStats), 10_000);
            case COLLATERAL -> progress(profile.getHighestSingleGameDamage(), 1_000_000_000L);
            case TWO_FATES -> profile.hasCompletedRegnumOfTwoCrowns() ? "Completed" : "Not completed";
            case CROWNED_HEIR -> profile.hasCompletedRegnumOblivionWithFourPlayers() ? "Completed" : "Not completed";
            default -> honorific.isPurchasable() ? "Purchasable" : "Challenge";
        };
    }

    public static void recordStarPieceSynthesis(UUID uuid, long amount) {
        getProfile(uuid).addStarPiecesSynthesized(amount);
        refreshAndSave(uuid);
    }

    public static void recordStarPiecesUsed(UUID uuid, long amount) {
        if (amount > 0) {
            getProfile(uuid).addStarPiecesUsed(amount);
            refreshAndSave(uuid);
        }
    }

    public static void recordSupplyDrops(UUID uuid, long amount) {
        if (amount > 0) {
            getProfile(uuid).addSupplyDropsRolled(amount);
            refreshAndSave(uuid);
        }
    }

    public static void recordAncientRenegadesFloor(UUID uuid, int floor) {
        if (floor > 0) {
            getProfile(uuid).recordAncientRenegadesFloor(floor);
            refreshAndSave(uuid);
        }
    }

    public static void recordSingleGameDamage(UUID uuid, long damage) {
        if (damage > 0) {
            getProfile(uuid).recordSingleGameDamage(damage);
            refreshAndSave(uuid);
        }
    }

    public static void recordRegnumCompletion(UUID uuid, boolean oblivion, int playerCount) {
        getProfile(uuid).recordRegnumCompletion(oblivion, playerCount);
        refreshAndSave(uuid);
    }

    public static void refreshDisplays(@Nullable Player player) {
        runSync(() -> {
            CustomScoreboard.updateLobbyPlayerNames();
            if (player != null) {
                player.playerListName(null);
            }
        });
    }

    @Nullable
    public static DatabasePlayer findDatabasePlayer(DatabasePlayerPvE pveStats) {
        return DatabaseManager.getLoadedPlayers(PlayersCollections.LIFETIME).values().stream()
                .filter(databasePlayer -> databasePlayer.getPveStats() == pveStats)
                .findFirst().orElse(null);
    }

    private static void refreshAndSave(UUID uuid) {
        if (DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            refreshChallengeUnlocks(DatabaseManager.getPlayer(uuid), Bukkit.getPlayer(uuid));
        }
        queueSave(uuid);
    }

    private static void queueSave(UUID uuid) {
        if (DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            DatabaseManager.queueUpdatePlayerAsync(DatabaseManager.getPlayer(uuid));
        }
    }

    private static void refreshChallengesIfDue(UUID uuid) {
        if (!honorificsEnabled() || !DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - LAST_CHALLENGE_REFRESH.getOrDefault(uuid, 0L) < REFRESH_INTERVAL_MILLIS) {
            return;
        }
        LAST_CHALLENGE_REFRESH.put(uuid, now);
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
        if (refreshChallengeUnlocks(databasePlayer, Bukkit.getPlayer(uuid))) {
            queueSave(uuid);
        }
    }

    private static boolean meetsRequirement(Honorific honorific, DatabasePlayer databasePlayer, HonorificProfile profile) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        return switch (honorific) {
            case THE_MIGHTY_ROLLER -> profile.getItemRerolls() >= 90;
            case SYNTHESIZER -> profile.getStarPiecesSynthesized() >= 100;
            case EXTREMA -> getFastestExtremeTicks(pveStats) > 0 && getFastestExtremeTicks(pveStats) <= 8L * 60 * 20;
            case ONE_FOR_ALL -> getMobKills(pveStats, Mob.ONE_OF_NINE) >= 1;
            case THE_HEART_THIEF -> getMobKills(pveStats, Mob.LILIUM) >= 1;
            case BOUNDLESS -> getHighestEndlessWave(pveStats) >= 200;
            case GOLEM -> getIronGolemKills(pveStats) >= 50_000;
            case GOD_OF_WAR -> getTotalMobKills(pveStats) >= 2_000_000;
            case EXPLORER -> profile.getHighestAncientRenegadesFloor() >= 30;
            case TREASURER -> profile.getHighestAncientRenegadesFloor() >= 100;
            case SLAUGHTERER -> getLongestOnslaughtTicks(pveStats) >= 180L * 60 * 20;
            case STAR_GUIDE -> profile.getStarPiecesUsed() >= 500;
            case SUPPLIER -> profile.getSupplyDropsRolled() >= 50_000;
            case PRESTIGIOUS -> getHighestPrestige(databasePlayer) >= 30;
            case SKELETRON -> getSkeletonKills(pveStats) >= 50_000;
            case CHAMPION -> getChampionKills(pveStats) >= 10_000;
            case COLLATERAL -> profile.getHighestSingleGameDamage() >= 1_000_000_000L;
            case TWO_FATES -> profile.hasCompletedRegnumOfTwoCrowns();
            case CROWNED_HEIR -> profile.hasCompletedRegnumOblivionWithFourPlayers();
            default -> false;
        };
    }

    private static long getMobKills(DatabasePlayerPvE pveStats, Mob mob) {
        if (mob.name == null || mob.name.isEmpty()) {
            return 0;
        }
        return pveStats.getMobKillCount(mob.name);
    }

    private static long getSkeletonKills(DatabasePlayerPvE pveStats) {
        return getMobGroupKills(pveStats, SKELETON_MOB_NAMES);
    }

    private static long getChampionKills(DatabasePlayerPvE pveStats) {
        return getMobGroupKills(pveStats, CHAMPION_MOB_NAMES);
    }

    private static long getIronGolemKills(DatabasePlayerPvE pveStats) {
        return getMobGroupKills(pveStats, IRON_GOLEM_MOB_NAMES);
    }

    private static long getMobGroupKills(DatabasePlayerPvE pveStats, Set<String> mobNames) {
        Map<String, Long> mobKills = pveStats.getMobKills();
        long sum = 0;
        for (String mobName : mobNames) {
            Long value = mobKills.get(mobName);
            if (value != null) {
                sum += value;
            }
        }
        return sum;
    }

    private static Set<String> mobDisplayNames(Mob[] mobs) {
        Set<String> names = new HashSet<>();
        for (Mob mob : mobs) {
            if (mob.name != null && !mob.name.isEmpty()) {
                names.add(mob.name);
            }
        }
        return Set.copyOf(names);
    }

    private static long getTotalMobKills(DatabasePlayerPvE pveStats) {
        return pveStats.getTotalMobKills();
    }

    private static long getFastestExtremeTicks(DatabasePlayerPvE pveStats) {
        return pveStats.getWaveDefenseStats().getExtremeStats().getFastestGameFinished();
    }

    private static int getHighestEndlessWave(DatabasePlayerPvE pveStats) {
        return pveStats.getWaveDefenseStats().getEndlessStats().getHighestWaveCleared();
    }

    private static long getLongestOnslaughtTicks(DatabasePlayerPvE pveStats) {
        return pveStats.getOnslaughtStats().getLongestTicksLived();
    }

    private static int getHighestPrestige(DatabasePlayer databasePlayer) {
        return Arrays.stream(Specializations.VALUES)
                .mapToInt(specialization -> databasePlayer.getSpec(specialization).getPrestige()).max().orElse(0);
    }

    private static String progress(long current, long required) {
        return NumberFormat.addCommas(Math.min(current, required)) + " / " + NumberFormat.addCommas(required);
    }

    private static String formatTicks(long ticks) {
        long totalSeconds = Math.max(0, ticks / 20);
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    private static void notifyUnlock(@Nullable Player player, Honorific honorific) {
        if (player == null) {
            return;
        }
        runSync(() -> {
            player.sendMessage(Component.text("Honorific unlocked: ", NamedTextColor.GOLD)
                    .append(Component.text("[" + honorific.getDisplayName() + "]", NamedTextColor.AQUA)));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
        });
    }

    private static void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(Warlords.getInstance(), runnable);
        }
    }

    public static boolean honorificsEnabled() {
        return honorificsEnabled(null);
    }

    public static boolean honorificsEnabled(@Nullable CommandSender sender) {
        return FeatureFlags.isFeatureEnabled(FeatureFlags.HONORIFICS, sender);
    }
}
