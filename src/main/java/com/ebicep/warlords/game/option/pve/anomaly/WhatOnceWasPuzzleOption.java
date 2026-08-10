package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
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

    private static final int VAULT_COUNT = 9;
    private static final int VAULT_DURATION_TICKS = 30 * GameRunnable.SECOND;
    private static final int CODE_REVEAL_TICKS = 8 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = 3 * GameRunnable.SECOND;
    private static final int INSIGNIA_PER_VAULT = 50_000;
    private static final double RUNE_DISPLAY_Y_OFFSET = 1.5;
    private static final float RUNE_DISPLAY_SCALE = 1.4f;
    private static final float RUNE_INTERACTION_WIDTH = 1.75f;
    private static final float RUNE_INTERACTION_HEIGHT = 2.5f;

    private final boolean[] cacheEligibility = new boolean[VAULT_COUNT];
    private final List<Entity> runeEntities = new ArrayList<>();
    private final Map<UUID, AncientRune> runeInteractions = new HashMap<>();

    private List<AncientVaultMarker> vaultMarkers = List.of();
    private int preparationTicks = START_DELAY_TICKS;
    private int activeVault = -1;
    private int vaultTicks;
    private int revealTicks;
    private int codeProgress;
    private boolean vaultRunning;
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
                if (warlordsEntity == null || warlordsEntity.getGame() != game) {
                    return;
                }
                AncientRune rune = runeInteractions.get(interaction.getUniqueId());
                if (rune == null) {
                    return;
                }
                event.setCancelled(true);
                enterRune(event.getPlayer(), rune);
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
        new GameRunnable(game) {
            @Override
            public void run() {
                beginVault(0);
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
                if (!vaultRunning) {
                    return;
                }

                vaultTicks++;
                if (revealTicks > 0) {
                    revealTicks--;
                }
                mobTick();
                if (vaultTicks % MOB_SPAWN_INTERVAL == 0 && mobCount() < getMaximumMobCount()) {
                    spawnPuzzleMob();
                }
                if (vaultTicks % 10 == 0) {
                    showPuzzleParticles();
                }
                if (vaultTicks >= VAULT_DURATION_TICKS) {
                    failVault();
                }
            }
        }.runTaskTimer(0, 1);
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
        vaultRunning = true;
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
        announce(Component.text("Memorize the ancient code, then right-click the floating runes in order.", NamedTextColor.AQUA));
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

    private void enterRune(org.bukkit.entity.Player player, AncientRune rune) {
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
        cacheEligibility[activeVault] = true;
        clearHostileMobs();
        grantVaultInsignia();
        announce(Component.text("Vault " + (activeVault + 1) + " opened. Reward cache unlocked!", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1.2f));
        scheduleNextVault();
    }

    private void grantVaultInsignia() {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.addCurrency(INSIGNIA_PER_VAULT);
            warlordsPlayer.sendMessage(Component.text("Vault reward: ", NamedTextColor.GREEN)
                    .append(Component.text("❂ " + NumberFormat.addCommas(INSIGNIA_PER_VAULT), NamedTextColor.GOLD)));
        });
    }

    private void failVault() {
        if (!vaultRunning) {
            return;
        }
        vaultRunning = false;
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
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 2, 0.8f);
        });
        Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, Team.RED));
    }

    private void scheduleNextVault() {
        int nextVault = activeVault + 1;
        new GameRunnable(game) {
            @Override
            public void run() {
                beginVault(nextVault);
            }
        }.runTaskLater(5 * GameRunnable.SECOND);
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
        int amount = 2 + activeVault;
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
