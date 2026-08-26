package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WhatOnceWasPuzzleOption extends AbstractAnomalyOption {

    private static final int VAULT_COUNT = 6;
    private static final int VAULT_DURATION_TICKS = 30 * GameRunnable.SECOND;
    private static final int CODE_REVEAL_TICKS = 5 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = GameRunnable.SECOND;
    private static final int INSIGNIA_PER_VAULT = 25_000;
    private static final int ELITE_GUARDIANS_PER_PLAYER = 4;
    private static final int CHAMPION_GUARDIANS_PER_PLAYER = 2;
    private static final int GUARDIAN_SPAWN_INTERVAL_TICKS = 4;
    private static final int VAULT_CHARGE_KILLS_REQUIRED = 20;
    private static final int PENALTY_MOB_CAP = 10;
    private static final double VAULT_CHARGE_PER_KILL = 5;
    private static final float MOB_DIFFICULTY_INCREASE_PER_CACHE = .3f;
    private static final double RUNE_DISPLAY_Y_OFFSET = 1.5;
    private static final float RUNE_DISPLAY_SCALE = 1.4f;
    private static final float RUNE_INTERACTION_WIDTH = 1.75f;
    private static final float RUNE_INTERACTION_HEIGHT = 2.5f;

    private final boolean[] cacheEligibility = new boolean[VAULT_COUNT / 2];
    private final List<Entity> runeEntities = new ArrayList<>();
    private final Map<UUID, AncientRune> runeInteractions = new HashMap<>();
    private final List<UUID> rewardEligiblePlayers = new ArrayList<>();

    private List<AncientVaultMarker> vaultMarkers = List.of();
    private int preparationTicks = START_DELAY_TICKS;
    private int activeVault = -1;
    private int vaultTicks;
    private int chargeTicks;
    private int vaultChargeKills;
    private int revealTicks;
    private int codeProgress;
    private int guardianSpawnTicks;
    private int eliteGuardiansRemainingToSpawn;
    private int championGuardiansRemainingToSpawn;
    private boolean chargingVault;
    private boolean vaultRunning;
    private boolean guardianWaveRunning;
    private boolean failed;
    private List<AncientRune> activeCode = List.of();

    @Override
    public void register(@Nonnull Game game) {
        super.register(game);
        vaultMarkers = game.getMarkers(AncientVaultMarker.class)
                .stream()
                .sorted(Comparator.comparingInt(AncientVaultMarker::getVaultIndex))
                .toList();
        for (int vaultIndex = 0; vaultIndex < VAULT_COUNT; vaultIndex++) {
            int finalVaultIndex = vaultIndex;
            long runeCount = game.getMarkers(AncientRuneMarker.class)
                    .stream()
                    .filter(marker -> marker.getVaultIndex() == finalVaultIndex)
                    .count();
            if (runeCount != AncientRune.VALUES.length) {
                throw new IllegalStateException("Vault " + vaultIndex + " requires one marker for every ancient rune");
            }
        }

        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onInteract(PlayerInteractEntityEvent event) {
                if (!vaultRunning
                        || event.getHand() != EquipmentSlot.HAND
                        || !(event.getRightClicked() instanceof Interaction interaction)) {
                    return;
                }
                WarlordsEntity warlordsEntity = Warlords.getPlayer(event.getPlayer());
                if (warlordsEntity == null
                        || warlordsEntity.getGame() != game
                        || game.getPlayerTeam(event.getPlayer().getUniqueId()) == null) {
                    return;
                }
                if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                    return;
                }
                AncientRune rune = runeInteractions.get(interaction.getUniqueId());
                if (rune == null) {
                    return;
                }
                event.setCancelled(true);
                enterRune(event.getPlayer(), rune);
            }

            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                if (event.getDeclaredWinner() == Team.RED) {
                    grantUnlockedCaches();
                }
            }
        });

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(5, "what_once_was_puzzle") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return getPuzzleScoreboard();
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        rewardEligiblePlayers.clear();
        game.warlordsPlayersWithoutSpectators()
                .filter(entry -> entry.getValue() == Team.BLUE)
                .map(Map.Entry::getKey)
                .forEach(rewardEligiblePlayers::add);

        new GameRunnable(game) {
            @Override
            public void run() {
                beginChargePhase(0);
            }
        }.runTaskLater(START_DELAY_TICKS);

        new GameRunnable(game) {
            @Override
            public void run() {
                if (completed) {
                    cancel();
                    return;
                }
                incrementTicks();
                if (preparationTicks > 0) {
                    preparationTicks--;
                }
                if (guardianWaveRunning) {
                    mobTick();
                    if (getGuardiansRemainingToSpawn() > 0) {
                        guardianSpawnTicks++;
                        if (guardianSpawnTicks >= GUARDIAN_SPAWN_INTERVAL_TICKS) {
                            guardianSpawnTicks = 0;
                            spawnNextGuardian();
                        }
                    }
                    if (getGuardiansRemainingToSpawn() == 0 && mobCount() == 0) {
                        completeGuardianWave();
                    }
                    return;
                }
                if (chargingVault) {
                    chargeTicks++;
                    mobTick();
                    if (chargeTicks % MOB_SPAWN_INTERVAL == 0 && mobCount() < getMaximumMobCount()) {
                        spawnPuzzleMob();
                    }
                    return;
                }
                if (!vaultRunning) {
                    return;
                }

                vaultTicks++;
                if (revealTicks > 0) {
                    revealTicks--;
                }
                mobTick();
                if (vaultTicks % 10 == 0) {
                    showPuzzleParticles();
                }
                if (vaultTicks >= VAULT_DURATION_TICKS) {
                    failVault();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void beginChargePhase(int vaultIndex) {
        if (completed) {
            return;
        }
        activeVault = vaultIndex;
        chargeTicks = 0;
        vaultChargeKills = 0;
        chargingVault = true;
        vaultRunning = false;
        guardianWaveRunning = false;
        removeRuneEntities();
        clearHostileMobs();

        game.forEachOnlinePlayer((player, team) -> {
            player.showTitle(Title.title(
                    Component.text("CHARGE VAULT " + (vaultIndex + 1), NamedTextColor.GOLD),
                    Component.text("Defeat enemies to restore its energy", NamedTextColor.AQUA),
                    Title.Times.times(Ticks.duration(5), Ticks.duration(60), Ticks.duration(10))
            ));
            player.playSound(player.getLocation(), "raid.church.dingalt", 1.5f, 1);
        });
        announce(Component.text("Defeat enemies to charge Vault " + (vaultIndex + 1) + ". Each kill restores energy.", NamedTextColor.AQUA));
    }

    private void beginVault(int vaultIndex) {
        if (completed) {
            return;
        }
        if (vaultIndex >= VAULT_COUNT) {
            removeRuneEntities();
            finishAnomaly(cacheEligibility, "The ancient code was deciphered.");
            return;
        }

        activeVault = vaultIndex;
        vaultTicks = 0;
        revealTicks = CODE_REVEAL_TICKS;
        codeProgress = 0;
        chargingVault = false;
        vaultRunning = true;
        guardianWaveRunning = false;
        clearHostileMobs();
        activeCode = generateCode(vaultIndex);
        spawnRuneEntities(vaultIndex);

        Component code = buildCodeComponent();
        game.forEachOnlinePlayer((player, team) -> {
            player.showTitle(Title.title(
                    Component.text("Vault " + (vaultIndex + 1), NamedTextColor.GOLD),
                    code,
                    Title.Times.times(Ticks.duration(5), Ticks.duration(100), Ticks.duration(10))
            ));
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 0.8f);
        });
        announce(Component.text("Vault fully charged. Memorize the ancient code, then right-click the floating runes in order.", NamedTextColor.AQUA));
    }

    @Override
    protected boolean handleSpecialDeath(WarlordsDeathEvent event) {
        if (!chargingVault || !(event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC)) {
            return false;
        }
        if (warlordsNPC.getGame() != game
                || warlordsNPC.getMob() == null
                || !getMobs().contains(warlordsNPC.getMob())) {
            return false;
        }

        vaultChargeKills = Math.min(VAULT_CHARGE_KILLS_REQUIRED, vaultChargeKills + 1);
        if (vaultChargeKills < VAULT_CHARGE_KILLS_REQUIRED) {
            return false;
        }

        chargingVault = false;
        announce(Component.text("Vault " + (activeVault + 1) + " is fully charged. The ancient code is revealing itself!", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1.3f));
        new GameRunnable(game) {
            @Override
            public void run() {
                beginVault(activeVault);
            }
        }.runTaskLater(1);
        return false;
    }

    @Override
    protected void modifyStats(WarlordsNPC warlordsNPC) {
        super.modifyStats(warlordsNPC);
        int cachesUnlocked = getCachesUnlocked();
        if (cachesUnlocked == 0) {
            return;
        }
        float multiplier = 1 + cachesUnlocked * MOB_DIFFICULTY_INCREASE_PER_CACHE;
        warlordsNPC.setMaxHealthAndHeal(warlordsNPC.getMaxBaseHealth() * multiplier);
        warlordsNPC.setMinMeleeDamage(Math.round(warlordsNPC.getMinMeleeDamage() * multiplier));
        warlordsNPC.setMaxMeleeDamage(Math.round(warlordsNPC.getMaxMeleeDamage() * multiplier));
    }

    private void spawnRuneEntities(int vaultIndex) {
        removeRuneEntities();
        game.getMarkers(AncientRuneMarker.class)
                .stream()
                .filter(marker -> marker.getVaultIndex() == vaultIndex)
                .sorted(Comparator.comparingInt(marker -> marker.getRune().ordinal()))
                .forEach(marker -> {
                    Location markerLocation = marker.getLocation().clone();
                    Location displayLocation = markerLocation.clone().add(0, RUNE_DISPLAY_Y_OFFSET, 0);

                    ItemDisplay display = markerLocation.getWorld().spawn(displayLocation, ItemDisplay.class);
                    display.setItemStack(new ItemStack(marker.getRune().getMaterial()));
                    display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
                    display.setBillboard(Display.Billboard.CENTER);
                    display.setBrightness(new Display.Brightness(15, 15));
                    display.setGlowing(true);
                    display.setGravity(false);
                    display.setInvulnerable(true);
                    display.setPersistent(false);
                    Transformation transformation = display.getTransformation();
                    transformation.getScale().set(RUNE_DISPLAY_SCALE);
                    display.setTransformation(transformation);

                    Interaction interaction = markerLocation.getWorld().spawn(markerLocation.clone().add(0, .25, 0), Interaction.class);
                    interaction.setInteractionWidth(RUNE_INTERACTION_WIDTH);
                    interaction.setInteractionHeight(RUNE_INTERACTION_HEIGHT);
                    interaction.setResponsive(true);
                    interaction.setGravity(false);
                    interaction.setInvulnerable(true);
                    interaction.setPersistent(false);

                    runeEntities.add(display);
                    runeEntities.add(interaction);
                    runeInteractions.put(interaction.getUniqueId(), marker.getRune());
                });
    }

    private void removeRuneEntities() {
        runeEntities.forEach(Entity::remove);
        runeEntities.clear();
        runeInteractions.clear();
    }

    private List<AncientRune> generateCode(int vaultIndex) {
        int codeLength = 3 + vaultIndex;
        long seed = rotationStart
                ^ game.getGameId().getMostSignificantBits()
                ^ game.getGameId().getLeastSignificantBits()
                ^ (0x574841544F4E4345L * (vaultIndex + 1));
        Random random = new Random(seed);
        List<AncientRune> code = new ArrayList<>(codeLength);
        while (code.size() < codeLength) {
            AncientRune rune = AncientRune.VALUES[random.nextInt(AncientRune.VALUES.length)];
            if (!code.isEmpty() && code.get(code.size() - 1) == rune) {
                continue;
            }
            code.add(rune);
        }
        return List.copyOf(code);
    }

    private void enterRune(Player player, AncientRune rune) {
        AncientRune expected = activeCode.get(codeProgress);
        if (rune != expected) {
            codeProgress = 0;
            revealTicks = 5 * GameRunnable.SECOND;
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0.7f);
            announce(Component.text("The code rejected " + rune.getName() + ". The sequence has reset!", NamedTextColor.RED));
            spawnPenaltyWave();
            return;
        }

        codeProgress++;
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.4f);
        announce(Component.text(player.getName() + " activated the " + rune.getName() + " rune (" + codeProgress + "/" + activeCode.size() + ").", NamedTextColor.GREEN));
        if (codeProgress >= activeCode.size()) {
            completeVault();
        }
    }

    private void completeVault() {
        if (!vaultRunning) {
            return;
        }
        vaultRunning = false;
        removeRuneEntities();
        int completedVault = activeVault + 1;
        boolean cacheUnlocked = completedVault % 2 == 0;
        if (cacheUnlocked) {
            cacheEligibility[activeVault / 2] = true;
        }
        clearHostileMobs();
        grantVaultInsignia();
        announce(Component.text("Vault " + completedVault + " opened." + (cacheUnlocked ? " Reward cache unlocked!" : ""), NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), "raid.piano.ding", 2, 0.7f));

        if (completedVault >= VAULT_COUNT) {
            finishAnomaly(cacheEligibility, "The ancient code was deciphered.");
            return;
        }
        startGuardianWave();
    }

    private void startGuardianWave() {
        guardianWaveRunning = true;
        guardianSpawnTicks = 0;
        int players = Math.max(1, playerCount());
        eliteGuardiansRemainingToSpawn = players * ELITE_GUARDIANS_PER_PLAYER;
        championGuardiansRemainingToSpawn = players * CHAMPION_GUARDIANS_PER_PLAYER;

        announce(Component.text("Ancient guardians bar the way to Vault " + (activeVault + 2) + ". Defeat them to continue!", NamedTextColor.RED));
        game.forEachOnlinePlayer((player, team) -> {
            player.showTitle(Title.title(
                    Component.text("FIGHT!", NamedTextColor.RED),
                    Component.text("Defeat the vault guardians", NamedTextColor.GOLD),
                    Title.Times.times(Ticks.duration(5), Ticks.duration(60), Ticks.duration(10))
            ));
            player.playSound(player.getLocation(), "misc.icewall", 2, 0.8f);
        });
    }

    private void spawnNextGuardian() {
        List<AnomalySpawnMarker> markers = getActiveSpawnMarkers();
        if (markers.isEmpty()) {
            eliteGuardiansRemainingToSpawn = 0;
            championGuardiansRemainingToSpawn = 0;
            return;
        }

        int guardiansRemaining = getGuardiansRemainingToSpawn();
        if (guardiansRemaining <= 0) {
            return;
        }

        Mob[] mobPool;
        if (eliteGuardiansRemainingToSpawn > 0
                && (championGuardiansRemainingToSpawn == 0
                || ThreadLocalRandom.current().nextInt(guardiansRemaining) < eliteGuardiansRemainingToSpawn)) {
            mobPool = Mob.ELITE;
            eliteGuardiansRemainingToSpawn--;
        } else {
            mobPool = Mob.CHAMPION;
            championGuardiansRemainingToSpawn--;
        }

        Mob mob = mobPool[ThreadLocalRandom.current().nextInt(mobPool.length)];
        Location location = markers.get(ThreadLocalRandom.current().nextInt(markers.size())).getLocation().clone();
        spawnNewMob(mob.createMob(location), Team.RED);
    }

    private int getGuardiansRemainingToSpawn() {
        return eliteGuardiansRemainingToSpawn + championGuardiansRemainingToSpawn;
    }

    private void completeGuardianWave() {
        if (!guardianWaveRunning) {
            return;
        }
        guardianWaveRunning = false;
        int nextVault = activeVault + 1;
        announce(Component.text("The vault guardians have fallen. Charge Vault " + (nextVault + 1) + " to reveal its code!", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.5f, 0.7f));
        beginChargePhase(nextVault);
    }

    private void grantVaultInsignia() {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.addCurrency(INSIGNIA_PER_VAULT);
            warlordsPlayer.sendMessage(Component.text("Vault reward: ", NamedTextColor.GREEN)
                    .append(Component.text("❂ " + NumberFormat.addCommas(INSIGNIA_PER_VAULT), NamedTextColor.GOLD)));
        });
    }

    private void grantUnlockedCaches() {
        int eligibleObjectiveCount = Math.min(cacheEligibility.length, currentAnomaly.getRewardPools().size());
        rewardEligiblePlayers.forEach(uuid -> {
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
            if (cachesGranted == 0) {
                return;
            }
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            RewardInventory.sendRewardMessage(
                    uuid,
                    Component.text(cachesGranted + " Anomaly Reward " + (cachesGranted == 1 ? "Cache is" : "Caches are") + " ready to claim.", NamedTextColor.AQUA)
            );
        });
    }

    private void failVault() {
        if (!vaultRunning) {
            return;
        }
        vaultRunning = false;
        guardianWaveRunning = false;
        failed = true;
        completed = true;
        removeRuneEntities();
        clearHostileMobs();
        announce(Component.text("Vault " + (activeVault + 1) + " sealed itself. The anomaly has failed.", NamedTextColor.RED));
        game.forEachOnlinePlayer((player, team) -> {
            player.showTitle(Title.title(
                    Component.text("VAULT FAILED", NamedTextColor.RED),
                    Component.text("The anomaly has ended.", NamedTextColor.GRAY),
                    Title.Times.times(Ticks.duration(5), Ticks.duration(60), Ticks.duration(10))
            ));
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 2, 0.7f);
        });
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.RED));
    }

    private void spawnPuzzleMob() {
        List<AnomalySpawnMarker> markers = getActiveSpawnMarkers();
        if (markers.isEmpty()) {
            return;
        }
        Location location = markers.get(ThreadLocalRandom.current().nextInt(markers.size())).getLocation().clone();
        spawnCurrentAnomalyMob(location);
    }

    private void spawnPenaltyWave() {
        List<AnomalySpawnMarker> markers = getActiveSpawnMarkers();
        if (markers.isEmpty()) {
            return;
        }

        int amount = Math.min(2 + activeVault, PENALTY_MOB_CAP - mobCount());
        for (int i = 0; i < amount; i++) {
            Location location = markers.get(ThreadLocalRandom.current().nextInt(markers.size())).getLocation().clone();
            spawnCurrentAnomalyMob(location);
        }
    }

    private List<AnomalySpawnMarker> getActiveSpawnMarkers() {
        return game.getMarkers(AnomalySpawnMarker.class)
                .stream()
                .filter(marker -> marker.getObjectiveIndex() == activeVault)
                .toList();
    }

    private void showPuzzleParticles() {
        AncientVaultMarker vault = vaultMarkers.get(activeVault);
        Location vaultLocation = vault.getLocation().clone().add(0, 1, 0);
        vaultLocation.getWorld().spawnParticle(Particle.END_ROD, vaultLocation, 8, .8, 1, .8, .02);

        for (AncientRuneMarker marker : game.getMarkers(AncientRuneMarker.class)) {
            if (marker.getVaultIndex() != activeVault) {
                continue;
            }
            Location runeLocation = marker.getLocation().clone().add(0, RUNE_DISPLAY_Y_OFFSET, 0);
            runeLocation.getWorld().spawnParticle(Particle.ENCHANT, runeLocation, 4, .3, .5, .3, .02);
        }
    }

    private int getMaximumMobCount() {
        return 5 + playerCount() * 3 + activeVault * 2;
    }

    private List<Component> getPuzzleScoreboard() {
        List<Component> lines = new ArrayList<>();
        if (completed) {
            lines.add(Component.text(failed ? "Vault Failed" : "Vaults Complete", failed ? NamedTextColor.RED : NamedTextColor.GREEN));
            return lines;
        }
        if (activeVault < 0) {
            int seconds = Math.max(0, (preparationTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
            lines.add(Component.text("Puzzle starts in: ", NamedTextColor.WHITE)
                    .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            return lines;
        }
        if (guardianWaveRunning) {
            lines.add(Component.text("Guardians: ", NamedTextColor.WHITE)
                    .append(Component.text((mobCount() + getGuardiansRemainingToSpawn()) + " remaining", NamedTextColor.GOLD)));
            lines.add(Component.text("Next Vault: ", NamedTextColor.WHITE)
                    .append(Component.text((activeVault + 2) + "/" + VAULT_COUNT, NamedTextColor.AQUA)));
            lines.add(Component.text("Caches: ", NamedTextColor.WHITE)
                    .append(Component.text(getCachesUnlocked() + "/3", NamedTextColor.GREEN)));
            return lines;
        }
        if (chargingVault) {
            lines.add(Component.text("Charging Vault ", NamedTextColor.GOLD)
                    .append(Component.text((activeVault + 1) + "/" + VAULT_COUNT, NamedTextColor.AQUA)));
            lines.add(Component.text("Vault Energy: ", NamedTextColor.WHITE)
                    .append(Component.text(NumberFormat.formatOptionalTenths(getVaultChargePercent()) + "%", NamedTextColor.YELLOW)));
            lines.add(Component.text("Enemies: ", NamedTextColor.WHITE)
                    .append(Component.text(mobCount() + " active", NamedTextColor.RED)));
            lines.add(Component.text("Caches: ", NamedTextColor.WHITE)
                    .append(Component.text(getCachesUnlocked() + "/3", NamedTextColor.GREEN)));
            return lines;
        }
        if (!vaultRunning) {
            lines.add(Component.text("Preparing Vault " + Math.min(activeVault + 2, VAULT_COUNT) + "...", NamedTextColor.YELLOW));
            return lines;
        }

        lines.add(Component.text("Vault: ", NamedTextColor.WHITE)
                .append(Component.text((activeVault + 1) + "/" + VAULT_COUNT, NamedTextColor.GOLD)));
        lines.add(Component.text("Seals in: ", NamedTextColor.WHITE)
                .append(Component.text(getSecondsRemaining() + "s", NamedTextColor.YELLOW)));
        if (revealTicks > 0) {
            lines.add(Component.text("Code: ", NamedTextColor.WHITE).append(buildCodeComponent()));
        } else {
            lines.add(Component.text("Code: Hidden", NamedTextColor.DARK_GRAY));
        }
        lines.add(Component.text("Input: ", NamedTextColor.WHITE)
                .append(Component.text(codeProgress + "/" + activeCode.size(), NamedTextColor.AQUA)));
        lines.add(Component.text("Caches: ", NamedTextColor.WHITE)
                .append(Component.text(getCachesUnlocked() + "/3", NamedTextColor.GREEN)));
        return lines;
    }

    private Component buildCodeComponent() {
        Component component = Component.empty();
        for (int i = 0; i < activeCode.size(); i++) {
            if (i > 0) {
                component = component.append(Component.space());
            }
            component = component.append(activeCode.get(i).getComponent());
        }
        return component;
    }

    private int getSecondsRemaining() {
        return Math.max(0, (VAULT_DURATION_TICKS - vaultTicks) / GameRunnable.SECOND);
    }

    private double getVaultChargePercent() {
        return Math.min(100, vaultChargeKills * VAULT_CHARGE_PER_KILL);
    }

    private int getCachesUnlocked() {
        int unlocked = 0;
        for (boolean cache : cacheEligibility) {
            if (cache) {
                unlocked++;
            }
        }
        return unlocked;
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        removeRuneEntities();
        super.onGameCleanup(game);
    }
}
