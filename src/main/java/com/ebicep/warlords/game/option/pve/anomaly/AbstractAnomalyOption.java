package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsGiveExperienceEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.ExperienceGainOption;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractAnomalyOption implements PveOption {

    protected static final int START_DELAY_TICKS = 10 * GameRunnable.SECOND;

    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger();
    private final List<UUID> rewardEligiblePlayers = new ArrayList<>();

    protected Game game;
    protected AnomalyRewards rewards;
    protected Anomalies currentAnomaly;
    protected NewItemsSetBonus featuredLegendarySet;
    protected long rotationStart;
    protected boolean completed;
    private int objectivesCompleted;
    private boolean cacheRewardsFinalized;
    private boolean successfulCompletion;
    private boolean[] finalCacheEligibility;
    private String completionSummary;
    private SimpleScoreboardHandler healthScoreboardHandler;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.currentAnomaly = AnomalyRotation.getCurrentAnomaly();
        for (Anomalies anomaly : Anomalies.VALUES) {
            if (anomaly.getMap() == game.getMap()) {
                this.currentAnomaly = anomaly;
                break;
            }
        }
        this.rotationStart = AnomalyRotation.getRotationStart().getEpochSecond();
        this.featuredLegendarySet = AnomalyRotation.getGuaranteedLegendarySet();
        this.rewards = new AnomalyRewards(this);

        game.registerEvents(getBaseListener());
        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onDeath(WarlordsDeathEvent event) {
                if (handleSpecialDeath(event)) {
                    return;
                }
                WarlordsEntity dead = event.getWarlordsEntity();
                if (!(dead instanceof WarlordsNPC warlordsNPC)) {
                    return;
                }
                AbstractMob mob = warlordsNPC.getMob();
                if (mob == null || !mobs.containsKey(mob)) {
                    return;
                }
                mob.onDeath(event.getKiller(), dead.getLocation(), AbstractAnomalyOption.this);
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        removeHostileMob(mob);
                    }
                }.runTaskLater(1);
            }

            @EventHandler
            public void onAddCurrency(WarlordsAddCurrencyFinalEvent event) {
                WarlordsEntity player = event.getWarlordsEntity();
                if (player.getGame() != game) {
                    return;
                }
                AbilityTree.handleAutoUpgrade(player);
            }

            @EventHandler
            public void onGiveExperience(WarlordsGiveExperienceEvent event) {
                WarlordsEntity player = event.getWarlordsEntity();
                if (player.getGame() != game) {
                    return;
                }
                ExperienceGainOption experienceGainOption = game.getOptions()
                        .stream()
                        .filter(ExperienceGainOption.class::isInstance)
                        .map(ExperienceGainOption.class::cast)
                        .findAny()
                        .orElse(null);
                if (experienceGainOption == null) {
                    return;
                }
                if (experienceGainOption.getPlayerExpPer() != 0 && objectivesCompleted > 0) {
                    event.getExperienceSummary().put("Objectives Completed", experienceGainOption.getPlayerExpPer() * objectivesCompleted);
                }
                if (experienceGainOption.getPlayerExpGameWinBonus() != 0 && successfulCompletion) {
                    event.getExperienceSummary().put(
                            "Anomaly Completion Bonus",
                            (long) (experienceGainOption.getPlayerExpGameWinBonus() * getDifficulty().getRewardsMultiplier())
                    );
                }
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                Team winner = event.getDeclaredWinner();
                if (winner == Team.BLUE || winner == Team.RED) {
                    finalizeCacheRewards(winner == Team.BLUE);
                }
            }
        });

        game.registerGameMarker(ScoreboardHandler.class, healthScoreboardHandler = new SimpleScoreboardHandler(6, "anomaly_players") {
            @Nonnull
            @Override
            public java.util.List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });
    }

    @Override
    public void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {
        rewardEligiblePlayers.clear();
        players.stream()
                .filter(WarlordsPlayer.class::isInstance)
                .map(WarlordsPlayer.class::cast)
                .filter(player -> player.getTeam() == Team.BLUE)
                .map(WarlordsPlayer::getUuid)
                .forEach(rewardEligiblePlayers::add);

        if (DatabaseManager.guildService == null) {
            return;
        }
        HashMap<Guild, HashSet<UUID>> guilds = new HashMap<>();
        List<UUID> uuids = game.playersWithoutSpectators().map(Map.Entry::getKey).toList();
        for (Guild guild : GuildManager.GUILDS) {
            for (UUID uuid : uuids) {
                Optional<GuildPlayer> guildPlayer = guild.getPlayerMatchingUUID(uuid);
                if (guildPlayer.isPresent() && guildPlayer.get().getJoinDate().isBefore(Instant.now().minus(2, ChronoUnit.DAYS))) {
                    guilds.computeIfAbsent(guild, key -> new HashSet<>()).add(uuid);
                }
            }
        }
        guilds.forEach((guild, validUUIDs) -> {
            for (AbstractGuildUpgrade<?> upgrade : guild.getUpgrades()) {
                upgrade.getUpgrade().onGame(game, validUUIDs, upgrade.getTier());
            }
        });
    }

    protected boolean handleSpecialDeath(WarlordsDeathEvent event) {
        return false;
    }

    protected boolean[] getCacheEligibility() {
        return new boolean[0];
    }

    protected void incrementTicks() {
        int elapsed = ticksElapsed.incrementAndGet();
        if (healthScoreboardHandler != null && elapsed % 10 == 0) {
            healthScoreboardHandler.markChanged();
        }
    }

    protected Mob getRandomAnomalyMob() {
        Mob[] spawnableMobs = currentAnomaly.getSpawnableMobs();
        return spawnableMobs[ThreadLocalRandom.current().nextInt(spawnableMobs.length)];
    }

    protected void spawnCurrentAnomalyMob(Location location) {
        spawnNewMob(getRandomAnomalyMob().createMob(location), Team.RED);
    }

    protected void finishAnomaly(boolean[] cacheEligibility, String summary) {
        if (completed) {
            return;
        }
        completed = true;
        successfulCompletion = true;
        finalCacheEligibility = cacheEligibility;
        completionSummary = summary;
        updateObjectivesCompleted(cacheEligibility);
        clearHostileMobs();
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.BLUE));
    }

    private void finalizeCacheRewards(boolean success) {
        if (cacheRewardsFinalized) {
            return;
        }
        cacheRewardsFinalized = true;

        boolean[] cacheEligibility = finalCacheEligibility == null ? getCacheEligibility() : finalCacheEligibility;
        updateObjectivesCompleted(cacheEligibility);
        int eligibleObjectiveCount = Math.min(cacheEligibility.length, currentAnomaly.getRewardPools().size());

        for (UUID uuid : rewardEligiblePlayers) {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
            int cachesGranted = 0;
            for (int i = 0; i < eligibleObjectiveCount; i++) {
                if (!cacheEligibility[i]) {
                    continue;
                }
                AnomalyRewardCache cache = currentAnomaly.getRewardPools().get(i)
                        .createCache(featuredLegendarySet, rotationStart);
                databasePlayer.getPveStats().getGameEventRewards().add(cache);
                cachesGranted++;
            }
            if (cachesGranted > 0) {
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                RewardInventory.sendRewardMessage(
                        uuid,
                        Component.text(cachesGranted + " Anomaly Reward " + (cachesGranted == 1 ? "Cache is" : "Caches are") + " ready to claim.", NamedTextColor.AQUA)
                );
            }

            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            if (success) {
                String summary = completionSummary == null ? "Anomaly completed." : completionSummary;
                player.sendMessage(Component.text(summary + " " + cachesGranted + "/3 reward caches were added to your Reward Inventory.", NamedTextColor.GREEN));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 1);
            } else if (cachesGranted > 0) {
                player.sendMessage(Component.text("The anomaly failed, but you retained " + cachesGranted + "/3 earned reward caches.", NamedTextColor.YELLOW));
            }
        }
    }

    private void updateObjectivesCompleted(boolean[] cacheEligibility) {
        objectivesCompleted = 0;
        int eligibleObjectiveCount = Math.min(cacheEligibility.length, currentAnomaly.getRewardPools().size());
        for (int i = 0; i < eligibleObjectiveCount; i++) {
            if (cacheEligibility[i]) {
                objectivesCompleted++;
            }
        }
    }

    protected void announce(Component component) {
        game.forEachOnlinePlayer((player, team) -> player.sendMessage(component));
    }

    protected void clearHostileMobs() {
        for (AbstractMob mob : new ArrayList<>(mobs.keySet())) {
            removeHostileMob(mob);
        }
    }

    protected void removeHostileMob(AbstractMob mob) {
        if (!mobs.containsKey(mob)) {
            return;
        }
        WarlordsNPC npc = mob.getWarlordsNPC();
        mob.cleanup(this);
        npc.cleanup();
        mobs.remove(mob);
        game.getPlayers().remove(npc.getUuid());
        Warlords.removePlayer(npc.getUuid());
        MobCommand.SPAWNED_MOBS.remove(mob);
    }

    public Anomalies getCurrentAnomaly() {
        return currentAnomaly;
    }

    public int getObjectivesCompleted() {
        return objectivesCompleted;
    }

    public boolean isCompleted() {
        return successfulCompletion;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        WarlordsNPC npc = mob.toNPC(game, team, AbstractAnomalyOption.this::modifyStats);
        game.addNPC(npc);
        mobs.put(mob, new MobData(ticksElapsed.get()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    protected void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(AbstractAnomalyOption.this);

        int playerCount = playerCount();
        boolean isNotSolo = playerCount > 1;
        /*
         * Base scale of 700
         *
         * The higher the scale is the longer it takes to increase per interval.
         */
        double scale = 700;
        // Flag check whether mob is a boss.
        boolean bossFlagCheck = isNotSolo && warlordsNPC.getMob() instanceof BossLike;
        // Reduce base scale by 75 for each player after 2 or more players in game instance.
        double modifiedScale = scale - (isNotSolo ? 75 * Math.min(6, playerCount) : 0);
        // Divide scale based on wave count.
        double modifier = ((double) ticksElapsed.get() / 1000) / modifiedScale + 1;
        // Multiply health & min/max melee damage by waveCounter + 1 ^ base damage.
        int minMeleeDamage = (int) Math.pow(warlordsNPC.getMinMeleeDamage(), modifier);
        int maxMeleeDamage = (int) Math.pow(warlordsNPC.getMaxMeleeDamage(), modifier);
        float health = (float) Math.pow(warlordsNPC.getMaxBaseHealth(), modifier);
        // Increase boss health by 25% for each player in game instance.
        float bossMultiplier = 1 + (0.25f * playerCount);

        if (warlordsNPC.getMob() instanceof PlayerMob) {
            warlordsNPC.setMaxHealthAndHeal(health);
            warlordsNPC.setMinMeleeDamage(minMeleeDamage);
            warlordsNPC.setMaxMeleeDamage(maxMeleeDamage);
            return;
        }

        // Final health value after applying all modifiers.
        float finalHealth = health * (bossFlagCheck ? bossMultiplier : 1);
        warlordsNPC.setMaxHealthAndHeal(finalHealth);
        warlordsNPC.setMinMeleeDamage(minMeleeDamage);
        warlordsNPC.setMaxMeleeDamage(maxMeleeDamage);
    }

    @Override
    public PveRewards<?> getRewards() {
        return rewards;
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        clearHostileMobs();
        PveOption.super.onGameCleanup(game);
    }
}