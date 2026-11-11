package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.featureflags.FeatureFlags;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerCompStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPubStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.TournamentStats;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
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
import java.util.List;
import java.util.Locale;
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
    private static final long COOKED_BURN_KILLS = 500;
    private static final long FROG_TRANSFORMATIONS = 30;
    private static final long FRACTAL_SINGLE_HIT_DAMAGE = 300_000;
    private static final long HEAVYWEIGHT_CTF_WINS = 100;
    private static final long YOU_NEED_ME_FLAG_RETURNS = 100;
    private static final long PINGUIN_FROZEN_KILLS = 3_000;
    private static final long SHRIMP_JUMPS = 3_000;
    private static final long PRISM_REFLECTIVE_KILLS = 500;
    private static final long UNSTABLE_CLEAN_CACHES = 120;
    private static final Bounty[] LIFETIME_BOUNTIES = Arrays.stream(Bounty.VALUES)
            .filter(bounty -> LifetimeCost.class.isAssignableFrom(bounty.clazz))
            .toArray(Bounty[]::new);
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
        if (!profile.hasMaxedPveWeapon() && hasMaxedPveWeapon(pveStats)) {
            profile.recordMaxedPveWeapon();
            changed = true;
        }
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
            case ONE_FOR_ALL -> progress(getBossDefeats(profile, pveStats, Mob.ONE_OF_NINE), 1);
            case THE_HEART_THIEF -> progress(getBossDefeats(profile, pveStats, Mob.LILIUM), 1);
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
            case LOSER -> progress(databasePlayer.getDeaths(), 1_000);
            case GOOBER -> profile.hasDiedToOwnDamage() ? "Completed" : "Not completed";
            case MONEYBAGS -> progress(profile.getHighestSingleGameCoins(), 200_000);
            case PICKLE -> profile.hasWonWithPlikie() ? "Completed" : "Not completed";
            case REAPER -> profile.hasSacrificedWithSacrifice() ? "Completed" : "Not completed";
            case NO_LIFE -> progress(databasePlayer.getPlays(), 10_000);
            case COOKED -> progress(profile.getBurnKills(), COOKED_BURN_KILLS);
            case FROG -> progress(profile.getFrogTransformations(), FROG_TRANSFORMATIONS);
            case FRACTAL -> progress(profile.getHighestSingleHitDamage(), FRACTAL_SINGLE_HIT_DAMAGE);
            case HEAVYWEIGHT -> progress(getCaptureTheFlagWins(databasePlayer), HEAVYWEIGHT_CTF_WINS);
            case YOU_NEED_ME -> progress(getCaptureTheFlagReturns(databasePlayer), YOU_NEED_ME_FLAG_RETURNS);
            case PERFECTED -> profile.hasMaxedPveWeapon() ? "Completed" : "Not completed";
            case PLANT -> "Not available yet";
            case PINGUIN -> progress(profile.getFrozenOrSlowedKills(), PINGUIN_FROZEN_KILLS);
            case SHRIMP -> progress(profile.getJumps(), SHRIMP_JUMPS);
            case PRISM -> progress(profile.getReflectiveKills(), PRISM_REFLECTIVE_KILLS);
            case UNSTABLE -> progress(profile.getCleanAnomalyCacheStreak(), UNSTABLE_CLEAN_CACHES);
            case WELLSPRING -> progress(getCompletedLifetimeBounties(databasePlayer), LIFETIME_BOUNTIES.length);
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

    public static void recordOwnDamageDeath(UUID uuid) {
        if (!honorificsEnabled()) {
            return;
        }
        getProfile(uuid).recordOwnDamageDeath();
        refreshAndSave(uuid);
    }

    public static void recordSingleGameCoins(UUID uuid, long coins) {
        if (coins > 0) {
            getProfile(uuid).recordSingleGameCoins(coins);
            refreshAndSave(uuid);
        }
    }

    public static void recordWinWithPlikie(UUID uuid) {
        getProfile(uuid).recordWinWithPlikie();
        refreshAndSave(uuid);
    }

    public static void recordSacrificeKill(UUID uuid) {
        if (!honorificsEnabled()) {
            return;
        }
        getProfile(uuid).recordSacrificeKill();
        refreshAndSave(uuid);
    }

    public static void recordFrogTransformation(UUID uuid) {
        if (!honorificsEnabled() || uuid == null) {
            return;
        }
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        DatabaseManager.getPlayer(uuid).getHonorifics().addFrogTransformation();
        refreshAndSave(uuid);
    }

    public static void recordSingleHit(WarlordsEntity source, WarlordsEntity target, long damage) {
        if (!honorificsEnabled() || damage <= 0 || !isEligibleCombatCredit(source, target)) {
            return;
        }
        UUID uuid = source.getUuid();
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        if (DatabaseManager.getPlayer(uuid).getHonorifics().recordSingleHitDamage(damage)) {
            refreshAndSave(uuid);
        }
    }

    public static void onEnemyDeath(@Nullable WarlordsEntity killer, @Nullable WarlordsEntity victim) {
        if (!honorificsEnabled() || !isEligibleCombatCredit(killer, victim)) {
            return;
        }
        boolean burning = hasBurnEffect(victim);
        boolean frozenOrSlowed = isFrozenOrSlowed(victim);
        if (!burning && !frozenOrSlowed) {
            return;
        }
        UUID killerId = killer.getUuid();
        Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> {
            if (victim.isDead()) {
                creditQualifiedKill(killerId, burning, frozenOrSlowed);
            }
        });
    }

    public static void recordJump(Player player) {
        if (!honorificsEnabled() || player == null) {
            return;
        }
        WarlordsEntity entity = Warlords.getPlayer(player);
        if (!(entity instanceof WarlordsPlayer)) {
            return;
        }
        Game game = entity.getGame();
        if (game == null || game.getGameMode() == GameMode.LOBBY || game.getAddons().contains(GameAddon.CUSTOM_GAME)) {
            return;
        }
        UUID uuid = entity.getUuid();
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificProfile profile = DatabaseManager.getPlayer(uuid).getHonorifics();
        profile.addJump();
        if (profile.getJumps() >= SHRIMP_JUMPS && !profile.isUnlocked(Honorific.SHRIMP)) {
            refreshAndSave(uuid);
        } else {
            queueSave(uuid);
        }
    }

    public static void recordReflectiveKill(WarlordsEntity source, WarlordsEntity target) {
        if (!honorificsEnabled() || !isEligibleCombatCredit(source, target)) {
            return;
        }
        UUID uuid = source.getUuid();
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        DatabaseManager.getPlayer(uuid).getHonorifics().addReflectiveKills(1);
        refreshAndSave(uuid);
    }

    public static void recordAnomalyCacheUnlock(DatabasePlayer databasePlayer, boolean legendaryOrSovereign) {
        if (!honorificsEnabled() || databasePlayer == null) {
            return;
        }
        UUID uuid = databasePlayer.getUuid();
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        if (DatabaseManager.getPlayer(uuid).getHonorifics().recordAnomalyCacheUnlock(legendaryOrSovereign)) {
            refreshAndSave(uuid);
        }
    }

    public static boolean isLegendaryOrSovereignItem(@Nullable NewItem item) {
        if (item == null || item.getSetBonus() == null) {
            return false;
        }
        NewItemTier tier = item.getTier();
        return tier == NewItemTier.LEGENDARY || tier == NewItemTier.SOVEREIGN;
    }

    public static void refreshAfterBountyClaim(DatabasePlayer databasePlayer) {
        if (!honorificsEnabled() || databasePlayer == null) {
            return;
        }
        forceChallengeRefresh(databasePlayer, Bukkit.getPlayer(databasePlayer.getUuid()));
    }

    public static void recordWeaponUpgrade(UUID uuid, @Nullable AbstractWeapon weapon) {
        if (!honorificsEnabled() || uuid == null || !(weapon instanceof Upgradeable upgradeable)) {
            return;
        }
        if (upgradeable.getUpgradeLevel() < upgradeable.getMaxUpgradeLevel()) {
            return;
        }
        recordMaxedPveWeapon(uuid);
    }

    public static void recordTeamBossDefeat(WarlordsEntity killer, Mob mob) {
        if (!honorificsEnabled() || killer == null || mob == null) {
            return;
        }
        if (mob != Mob.ONE_OF_NINE && mob != Mob.LILIUM) {
            return;
        }
        Game game = killer.getGame();
        if (game == null || game.getAddons().contains(GameAddon.CUSTOM_GAME)) {
            return;
        }
        game.warlordsPlayers()
                .filter(killer::isTeammate)
                .forEach(teammate -> creditTeamBossDefeat(teammate.getUuid(), mob));
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
            case ONE_FOR_ALL -> getBossDefeats(profile, pveStats, Mob.ONE_OF_NINE) >= 1;
            case THE_HEART_THIEF -> getBossDefeats(profile, pveStats, Mob.LILIUM) >= 1;
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
            case LOSER -> databasePlayer.getDeaths() >= 1_000;
            case GOOBER -> profile.hasDiedToOwnDamage();
            case MONEYBAGS -> profile.getHighestSingleGameCoins() >= 200_000;
            case PICKLE -> profile.hasWonWithPlikie();
            case REAPER -> profile.hasSacrificedWithSacrifice();
            case NO_LIFE -> databasePlayer.getPlays() >= 10_000;
            case COOKED -> profile.getBurnKills() >= COOKED_BURN_KILLS;
            case FROG -> profile.getFrogTransformations() >= FROG_TRANSFORMATIONS;
            case FRACTAL -> profile.getHighestSingleHitDamage() >= FRACTAL_SINGLE_HIT_DAMAGE;
            case HEAVYWEIGHT -> getCaptureTheFlagWins(databasePlayer) >= HEAVYWEIGHT_CTF_WINS;
            case YOU_NEED_ME -> getCaptureTheFlagReturns(databasePlayer) >= YOU_NEED_ME_FLAG_RETURNS;
            case PERFECTED -> profile.hasMaxedPveWeapon();
            case PLANT -> false;
            case PINGUIN -> profile.getFrozenOrSlowedKills() >= PINGUIN_FROZEN_KILLS;
            case SHRIMP -> profile.getJumps() >= SHRIMP_JUMPS;
            case PRISM -> profile.getReflectiveKills() >= PRISM_REFLECTIVE_KILLS;
            case UNSTABLE -> profile.getCleanAnomalyCacheStreak() >= UNSTABLE_CLEAN_CACHES;
            case WELLSPRING -> LIFETIME_BOUNTIES.length > 0 && getCompletedLifetimeBounties(databasePlayer) >= LIFETIME_BOUNTIES.length;
            default -> false;
        };
    }

    private static void recordMaxedPveWeapon(UUID uuid) {
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificProfile profile = DatabaseManager.getPlayer(uuid).getHonorifics();
        if (profile.hasMaxedPveWeapon()) {
            if (refreshChallengeUnlocks(DatabaseManager.getPlayer(uuid), Bukkit.getPlayer(uuid))) {
                queueSave(uuid);
            }
            return;
        }
        profile.recordMaxedPveWeapon();
        refreshAndSave(uuid);
    }

    private static void creditQualifiedKill(UUID uuid, boolean burning, boolean frozenOrSlowed) {
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificProfile profile = DatabaseManager.getPlayer(uuid).getHonorifics();
        if (burning) {
            profile.addBurnKills(1);
        }
        if (frozenOrSlowed) {
            profile.addFrozenOrSlowedKills(1);
        }
        refreshAndSave(uuid);
    }

    private static boolean isEligibleCombatCredit(@Nullable WarlordsEntity source, @Nullable WarlordsEntity target) {
        if (!(source instanceof WarlordsPlayer) || target == null || source == target || !target.isEnemy(source)) {
            return false;
        }
        Game game = source.getGame();
        return game != null && !game.getAddons().contains(GameAddon.CUSTOM_GAME);
    }

    private static boolean hasBurnEffect(WarlordsEntity entity) {
        for (AbstractCooldown<?> cooldown : entity.getCooldownManager().getCooldowns()) {
            String name = cooldown.getName();
            if (name != null && name.toLowerCase(Locale.ROOT).contains("burn")) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFrozenOrSlowed(WarlordsEntity entity) {
        if (entity instanceof WarlordsNPC npc && npc.getStunTicks() > 0) {
            return true;
        }
        if (entity instanceof WarlordsPlayer && WarlordsPlayer.STUNNED_PLAYERS.contains(entity.getUuid())) {
            return true;
        }
        if (entity.getSpeed() == null) {
            return false;
        }
        for (MotionModifier modifier : entity.getSpeed().getModifiers()) {
            if (modifier.getModifier() < 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasMaxedPveWeapon(DatabasePlayerPvE pveStats) {
        List<AbstractWeapon> weapons = pveStats.getWeaponInventory();
        if (weapons == null) {
            return false;
        }
        for (AbstractWeapon weapon : weapons) {
            if (weapon instanceof Upgradeable upgradeable && upgradeable.getUpgradeLevel() >= upgradeable.getMaxUpgradeLevel()) {
                return true;
            }
        }
        return false;
    }

    private static int getCompletedLifetimeBounties(DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        if (pveStats == null) {
            return 0;
        }
        Set<Bounty> completed = new HashSet<>();
        Map<Bounty, Long> claimed = pveStats.getCompletedBounties();
        if (claimed != null) {
            for (Bounty bounty : LIFETIME_BOUNTIES) {
                Long count = claimed.get(bounty);
                if (count != null && count > 0) {
                    completed.add(bounty);
                }
            }
        }
        List<AbstractBounty> activeBounties = pveStats.getActiveBounties();
        if (activeBounties != null) {
            for (AbstractBounty bounty : activeBounties) {
                if (bounty == null || bounty.getBounty() == null) {
                    continue;
                }
                if (!LifetimeCost.class.isAssignableFrom(bounty.getBounty().clazz)) {
                    continue;
                }
                if (bounty.getValue() >= bounty.getTarget()) {
                    completed.add(bounty.getBounty());
                }
            }
        }
        return completed.size();
    }

    private static long getCaptureTheFlagWins(DatabasePlayer databasePlayer) {
        return sumCaptureTheFlagStat(databasePlayer, true);
    }

    private static long getCaptureTheFlagReturns(DatabasePlayer databasePlayer) {
        return sumCaptureTheFlagStat(databasePlayer, false);
    }

    private static long sumCaptureTheFlagStat(DatabasePlayer databasePlayer, boolean wins) {
        long total = 0;
        DatabasePlayerPubStats pubStats = databasePlayer.getPubStats();
        if (pubStats != null) {
            total += captureTheFlagStat(pubStats.getCtfStats(), wins);
        }
        DatabasePlayerCompStats compStats = databasePlayer.getCompStats();
        if (compStats != null) {
            total += captureTheFlagStat(compStats.getCtfStats(), wins);
        }
        TournamentStats tournamentStats = databasePlayer.getTournamentStats();
        if (tournamentStats != null) {
            for (TournamentStats.DatabasePlayerTournamentStats snapshot : tournamentStats.getAllTournamentStats()) {
                if (snapshot != null) {
                    total += captureTheFlagStat(snapshot.getCtfStats(), wins);
                }
            }
        }
        return total;
    }

    private static long captureTheFlagStat(@Nullable DatabasePlayerCTF ctfStats, boolean wins) {
        if (ctfStats == null) {
            return 0;
        }
        return wins ? ctfStats.getWins() : ctfStats.getFlagsReturned();
    }

    private static void creditTeamBossDefeat(UUID uuid, Mob mob) {
        if (!DatabaseManager.inCache(uuid, PlayersCollections.LIFETIME)) {
            return;
        }
        HonorificProfile profile = DatabaseManager.getPlayer(uuid).getHonorifics();
        if (!profile.recordTeamBossDefeat(mob.name())) {
            return;
        }
        refreshAndSave(uuid);
    }

    private static long getBossDefeats(HonorificProfile profile, DatabasePlayerPvE pveStats, Mob mob) {
        if (profile.hasTeamBossDefeat(mob.name())) {
            return 1;
        }
        long participation = getMobKills(pveStats, mob) + getMobAssists(pveStats, mob);
        return participation > 0 ? 1 : 0;
    }

    private static long getMobKills(DatabasePlayerPvE pveStats, Mob mob) {
        if (mob.name == null || mob.name.isEmpty()) {
            return 0;
        }
        return pveStats.getMobKillCount(mob.name);
    }

    private static long getMobAssists(DatabasePlayerPvE pveStats, Mob mob) {
        if (mob.name == null || mob.name.isEmpty()) {
            return 0;
        }
        Long assists = pveStats.getMobAssists().get(mob.name);
        return assists == null ? 0 : assists;
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
