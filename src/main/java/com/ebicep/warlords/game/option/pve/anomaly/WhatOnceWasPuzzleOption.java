package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WhatOnceWasPuzzleOption extends AbstractAnomalyOption {

    private static final int VAULT_COUNT = 3;
    private static final int VAULT_DURATION_TICKS = 120 * GameRunnable.SECOND;
    private static final int CODE_REVEAL_TICKS = 8 * GameRunnable.SECOND;
    private static final int MOB_SPAWN_INTERVAL = 3 * GameRunnable.SECOND;

    private final boolean[] cacheEligibility = new boolean[VAULT_COUNT];

    private List<AncientVaultMarker> vaultMarkers = List.of();
    private int preparationTicks = START_DELAY_TICKS;
    private int activeVault = -1;
    private int vaultTicks;
    private int revealTicks;
    private int codeProgress;
    private boolean vaultRunning;
    private List<AncientRune> activeCode = List.of();

    @Override
    public void register(@Nonnull Game game) {
        super.register(game);
        vaultMarkers = game.getMarkers(AncientVaultMarker.class)
                .stream()
                .sorted(Comparator.comparingInt(AncientVaultMarker::getVaultIndex))
                .toList();
        if (vaultMarkers.size() != VAULT_COUNT) {
            throw new IllegalStateException("What Once Was requires exactly three vault markers");
        }
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
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || !vaultRunning) {
                    return;
                }
                WarlordsEntity warlordsEntity = Warlords.getPlayer(event.getPlayer());
                if (warlordsEntity == null || warlordsEntity.getGame() != game) {
                    return;
                }
                AncientRuneMarker marker = findRuneMarker(event.getClickedBlock());
                if (marker == null) {
                    return;
                }
                event.setCancelled(true);
                enterRune(event.getPlayer(), marker.getRune());
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
        if (vaultIndex >= VAULT_COUNT) {
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

        Component code = buildCodeComponent();
        game.forEachOnlinePlayer((player, team) -> {
            player.showTitle(Title.title(
                    Component.text("Vault " + (vaultIndex + 1), NamedTextColor.GOLD),
                    code,
                    Title.Times.times(Ticks.duration(5), Ticks.duration(100), Ticks.duration(10))
            ));
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 0.8f);
        });
        announce(Component.text("Memorize the ancient code, then activate the rune pedestals in order.", NamedTextColor.AQUA));
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
        cacheEligibility[activeVault] = true;
        clearHostileMobs();
        announce(Component.text("Vault " + (activeVault + 1) + " opened. Reward cache unlocked!", NamedTextColor.GREEN));
        game.forEachOnlinePlayer((player, team) -> player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 1.2f));
        scheduleNextVault();
    }

    private void failVault() {
        if (!vaultRunning) {
            return;
        }
        vaultRunning = false;
        clearHostileMobs();
        announce(Component.text("Vault " + (activeVault + 1) + " sealed itself. Its reward cache was lost.", NamedTextColor.RED));
        scheduleNextVault();
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

    private AncientRuneMarker findRuneMarker(Block block) {
        return game.getMarkers(AncientRuneMarker.class)
                .stream()
                .filter(marker -> marker.getVaultIndex() == activeVault)
                .filter(marker -> sameBlock(marker.getLocation(), block.getLocation()))
                .findFirst()
                .orElse(null);
    }

    private boolean sameBlock(Location first, Location second) {
        return first.getWorld().equals(second.getWorld())
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
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
            Location runeLocation = marker.getLocation().clone().add(0, 1, 0);
            runeLocation.getWorld().spawnParticle(Particle.ENCHANT, runeLocation, 4, .3, .5, .3, .02);
        }
    }

    private int getMaximumMobCount() {
        return 5 + playerCount() * 3 + activeVault * 2;
    }

    private List<Component> getPuzzleScoreboard() {
        List<Component> lines = new ArrayList<>();
        if (completed) {
            lines.add(Component.text("Vaults Complete", NamedTextColor.GREEN));
            return lines;
        }
        if (activeVault < 0) {
            int seconds = Math.max(0, (preparationTicks + GameRunnable.SECOND - 1) / GameRunnable.SECOND);
            lines.add(Component.text("Puzzle starts in: ", NamedTextColor.GRAY)
                    .append(Component.text(seconds + "s", NamedTextColor.YELLOW)));
            return lines;
        }
        if (!vaultRunning) {
            lines.add(Component.text("Preparing Vault " + Math.min(activeVault + 2, VAULT_COUNT) + "...", NamedTextColor.YELLOW));
            return lines;
        }

        lines.add(Component.text("Vault: ", NamedTextColor.GRAY)
                .append(Component.text((activeVault + 1) + "/" + VAULT_COUNT, NamedTextColor.GOLD)));
        lines.add(Component.text("Seals in: ", NamedTextColor.GRAY)
                .append(Component.text(getSecondsRemaining() + "s", NamedTextColor.YELLOW)));
        if (revealTicks > 0) {
            lines.add(Component.text("Code: ", NamedTextColor.GRAY).append(buildCodeComponent()));
        } else {
            lines.add(Component.text("Code: Hidden", NamedTextColor.DARK_GRAY));
        }
        lines.add(Component.text("Input: ", NamedTextColor.GRAY)
                .append(Component.text(codeProgress + "/" + activeCode.size(), NamedTextColor.AQUA)));
        lines.add(Component.text("Caches: ", NamedTextColor.GRAY)
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
}