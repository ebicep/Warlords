package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Lilium — Petal Storm (with Lane Mode)
 *
 * Rains telegraphed petals over a rectangular arena in waves.
 * Lane Mode snaps impacts to evenly spaced lanes along X or Z for bullet-hell patterns.
 *
 * Constructor-only configuration (CrystalConduitsAbility style).
 */
public class PetalStormAbility {

    // --- Context ---
    private final WarlordsNPC source;
    private final Supplier<Location> centerSupplier;

    // --- Pattern / timing ---
    private final int waves;
    private final int petalsPerWave;
    private final int ticksBetweenWaves;
    private final int telegraphTicks;
    private final int fallTicks;
    private final double areaHalfSizeX;
    private final double areaHalfSizeZ;
    private final double fallHeight;

    // --- Impact / damage ---
    private final double impactRadius;
    private final float damageMin;
    private final float damageMax;
    private final boolean applyBlindness;
    private final int blindnessTicks;
    private final boolean applySlow;
    private final int slowTicks;
    private final int slowPercent;

    // --- Lane Mode ---
    private final boolean laneMode;       // enable/disable lane logic
    private final char laneAxis;          // 'X' or 'Z'
    private final int laneCount;          // >= 2
    private final String lanePattern;     // "SEQUENTIAL" | "ALTERNATING" | "RANDOM"
    private final int laneShiftPerWave;   // shift pattern each wave (can be negative)

    // --- Visuals / SFX ---
    private final Particle telegraphParticle = Particle.DUST;
    private final DustOptions telegraphDust = new DustOptions(Color.fromRGB(255, 105, 180), 1.05f);
    private final Particle fallTrail = Particle.SPORE_BLOSSOM_AIR;
    private final Particle popA = Particle.HEART;
    private final Particle popB = Particle.BLOCK_CRUMBLE;
    private final BlockData popBlock = Material.PINK_CONCRETE.createBlockData();
    private final Sound waveStartSfx = Sound.BLOCK_AMETHYST_BLOCK_RESONATE;
    private final Sound telegraphSfx = Sound.BLOCK_AMETHYST_CLUSTER_PLACE;
    private final Sound impactSfx = Sound.ENTITY_GENERIC_EXPLODE;

    private final Random rng = new Random();

    public PetalStormAbility(
            @Nonnull WarlordsNPC source,
            @Nonnull Supplier<Location> centerSupplier,
            // pattern / timing
            int waves,
            int petalsPerWave,
            int ticksBetweenWaves,
            int telegraphTicks,
            int fallTicks,
            double areaHalfSizeX,
            double areaHalfSizeZ,
            double fallHeight,
            // impact / damage
            double impactRadius,
            float damageMin,
            float damageMax,
            boolean applyBlindness,
            int blindnessTicks,
            boolean applySlow,
            int slowTicks,
            int slowPercent,
            // lane mode
            boolean laneMode,
            char laneAxis,
            int laneCount,
            @Nonnull String lanePattern,
            int laneShiftPerWave
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        // pattern / timing
        this.waves = Math.max(1, waves);
        this.petalsPerWave = Math.max(1, petalsPerWave);
        this.ticksBetweenWaves = Math.max(1, ticksBetweenWaves);
        this.telegraphTicks = Math.max(5, telegraphTicks);
        this.fallTicks = Math.max(5, fallTicks);
        this.areaHalfSizeX = Math.max(1.0, areaHalfSizeX);
        this.areaHalfSizeZ = Math.max(1.0, areaHalfSizeZ);
        this.fallHeight = Math.max(2.0, fallHeight);

        // impact / damage
        this.impactRadius = Math.max(0.5, impactRadius);
        this.damageMin = Math.max(0f, damageMin);
        this.damageMax = Math.max(this.damageMin, damageMax);
        this.applyBlindness = applyBlindness;
        this.blindnessTicks = Math.max(0, blindnessTicks);
        this.applySlow = applySlow;
        this.slowTicks = Math.max(0, slowTicks);
        this.slowPercent = Math.min(Math.max(slowPercent, 0), 100);

        // lane mode
        this.laneMode = laneMode;
        this.laneAxis = (laneAxis == 'Z' || laneAxis == 'z') ? 'Z' : 'X';
        this.laneCount = Math.max(2, laneCount);
        String norm = lanePattern == null ? "SEQUENTIAL" : lanePattern.toUpperCase();
        this.lanePattern = (norm.equals("ALTERNATING") || norm.equals("RANDOM")) ? norm : "SEQUENTIAL";
        this.laneShiftPerWave = laneShiftPerWave;
    }

    /** Triggers the whole storm sequence (waves looped with delays). */
    public void cast() {
        final Location center = groundSnap(centerSupplier.get().clone());
        if (center.getWorld() == null) return;

        center.getWorld().playSound(center, waveStartSfx, 2, 1.0f);

        new GameRunnable(source.getGame()) {
            int waveIndex = 0;
            int cooldown = 0;

            @Override
            public void run() {
                if (waveIndex >= waves) {
                    this.cancel();
                    return;
                }

                if (cooldown > 0) {
                    cooldown--;
                    return;
                }

                spawnWave(center.clone(), waveIndex);
                waveIndex++;
                cooldown = ticksBetweenWaves;
            }
        }.runTaskTimer(0, 1);
    }

    // Spawns one wave with multiple petals (telegraph -> fall -> impact)
    private void spawnWave(Location waveCenter, int waveIndex) {
        List<Location> targets = laneMode
                ? laneTargets(waveCenter, waveIndex)
                : randomTargets(waveCenter);

        // Telegraph runnable, then falling visual, then impact
        for (Location target : targets) {
            target.getWorld().playSound(target, telegraphSfx, 2, 1.4f);

            new GameRunnable(source.getGame()) {
                int t = 0;

                @Override
                public void run() {
                    if (t < telegraphTicks) {
                        drawRingDust(target, impactRadius, 20, telegraphDust);
                        t++;
                        return;
                    }

                    // Falling visual
                    final Location from = target.clone().add(0, fallHeight, 0);
                    final int total = fallTicks;

                    new GameRunnable(source.getGame()) {
                        int f = 0;

                        @Override
                        public void run() {
                            if (f >= total) {
                                doImpact(target);
                                this.cancel();
                                return;
                            }
                            double prog = (double) f / total; // 0..1
                            double y = from.getY() + (target.getY() - from.getY()) * prog;
                            Location mid = new Location(target.getWorld(), target.getX(), y, target.getZ());
                            target.getWorld().spawnParticle(fallTrail, mid, 4, 0.15, 0.15, 0.15, 0.0);
                            f++;
                        }
                    }.runTaskTimer(0, 1);

                    this.cancel();
                }
            }.runTaskTimer(0, 1);
        }
    }

    private List<Location> randomTargets(Location center) {
        List<Location> list = new ArrayList<>(petalsPerWave);
        for (int i = 0; i < petalsPerWave; i++) {
            double ox = (rng.nextDouble() * 2 - 1) * areaHalfSizeX;
            double oz = (rng.nextDouble() * 2 - 1) * areaHalfSizeZ;
            Location at = new Location(center.getWorld(), center.getX() + ox, center.getY(), center.getZ() + oz);
            list.add(groundSnap(at));
        }
        return list;
    }

    /**
     * Lane targets: evenly spaced coordinates along the chosen axis.
     * Lanes cover the full width: [-halfSize, +halfSize], centers at equal intervals.
     * Pattern controls the order lanes are filled inside a wave.
     */
    private List<Location> laneTargets(Location center, int waveIndex) {
        final double half = (laneAxis == 'X') ? areaHalfSizeX : areaHalfSizeZ;
        final double laneStep = (laneCount <= 1) ? 0.0 : (2 * half) / (laneCount - 1);

        // Build a safe lane order (SEQUENTIAL | ALTERNATING (center-out) | RANDOM)
        final int[] order = buildLaneOrder(laneCount, lanePattern);

        // Shift lanes per wave (wrap safely)
        final int shift = mod(laneShiftPerWave * waveIndex, laneCount);

        final List<Location> targets = new ArrayList<>(petalsPerWave);
        int laneIdx = 0;

        for (int i = 0; i < petalsPerWave; i++) {
            // pick lane with shift applied
            int base = order[laneIdx % laneCount];
            int lane = mod(base + shift, laneCount);
            laneIdx++;

            double laneCoord = -half + lane * laneStep;

            double x = center.getX();
            double z = center.getZ();

            if (laneAxis == 'X') {
                x += laneCoord;
                // free placement within Z bounds
                double oz = (rng.nextDouble() * 2 - 1) * areaHalfSizeZ;
                z += oz;
            } else { // 'Z'
                z += laneCoord;
                double ox = (rng.nextDouble() * 2 - 1) * areaHalfSizeX;
                x += ox;
            }

            Location at = new Location(center.getWorld(), x, center.getY(), z);
            targets.add(groundSnap(at));
        }
        return targets;
    }

    private int[] buildLaneOrder(int n, String pattern) {
        // base indices 0..n-1
        List<Integer> lanes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) lanes.add(i);

        String p = (pattern == null) ? "SEQUENTIAL" : pattern.toUpperCase();

        switch (p) {
            case "RANDOM":
                // Fisher–Yates via Collections.shuffle
                java.util.Collections.shuffle(lanes, rng);
                break;

            case "ALTERNATING":
                // Center-out order, works for even & odd n:
                // sort by distance to center, tie-break by lower index
                final double centerIdx = (n - 1) / 2.0;
                lanes.sort((a, b) -> {
                    double da = Math.abs(a - centerIdx);
                    double db = Math.abs(b - centerIdx);
                    int cmp = Double.compare(da, db);
                    return (cmp != 0) ? cmp : Integer.compare(a, b);
                });
                break;

            default: // "SEQUENTIAL"
                // keep 0..n-1 as-is
                break;
        }

        // materialize into array
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = lanes.get(i);
        return order;
    }

    // Apply damage + debuffs to enemies in radius, show pop VFX/SFX
    private void doImpact(Location at) {
        at.getWorld().playSound(at, impactSfx, 2, 1.0f);
        at.getWorld().spawnParticle(popA, at, 20, 0.4, 0.2, 0.4, 0.0);
        at.getWorld().spawnParticle(popB, at, 36, 0.6, 0.3, 0.6, 0.05, popBlock);

        PlayerFilter.entitiesAround(at, impactRadius, 3, impactRadius)
                .aliveEnemiesOf(source)
                .forEach(wp -> {
                    wp.addInstance(InstanceBuilder
                            .damage()
                            .cause("Petal Storm")
                            .source(source)
                            .min(damageMin)
                            .max(damageMax)
                    );

                    if (applyBlindness && blindnessTicks > 0) {
                        try {
                            wp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessTicks, 0, true, false));
                        } catch (Throwable ignored) {}
                    }

                    if (applySlow && slowPercent > 0 && slowTicks > 0) {
                        wp.addSpeedModifier(source, "Petal Storm", -slowPercent, slowTicks);
                    }
                });
    }

    // --- helpers ---

    private static int mod(int a, int m) {
        int r = a % m;
        return r < 0 ? r + m : r;
    }

    private static Location groundSnap(Location loc) {
        Location scan = loc.clone();
        for (int i = 0; i < 16; i++) {
            if (scan.getBlock().getType().isSolid()) {
                return scan.add(0, 1, 0);
            }
            scan.subtract(0, 1, 0);
        }
        return loc;
    }

    private static void drawRingDust(Location center, double radius, int points, DustOptions dust) {
        final double step = (Math.PI * 2) / points;
        for (int i = 0; i < points; i++) {
            double angle = i * step;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            Location p = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnParticle(Particle.DUST, p, 1, dust);
        }
    }
}
