package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

/**
 * Lilium — Bouquet Waltz
 *
 * Lilium dashes along a bezier-like arc from start -> end, dropping mini-bouquets at intervals.
 * Each bouquet emits an expanding petal ring that hits enemies once (damage + optional slow/knockback).
 *
 * Constructor-only config (CrystalConduitsAbility style).
 */
public class BouquetWaltzAbility {

    // ---- Context ----
    private final WarlordsNPC source;
    private final Supplier<Location> startSupplier;  // where the dash begins (often source::getLocation)
    private final Supplier<Location> endSupplier;    // where to dash to (arena point, player position, etc.)

    // ---- Dash / path knobs ----
    private final int dashTicks;                     // duration of the dash (ticks)
    private final double arcHeight;                  // vertical arc peak offset above the higher of start/end
    private final int dropEveryTicks;                // drop a bouquet every N ticks along the path
    private final boolean glideDuringDash;           // if true, suppress gravity-like drops (pure path follow)

    // ---- Ring / bouquet knobs ----
    private final int bouquetLifetimeTicks;          // lifetime for each bouquet’s expanding ring
    private final double ringStartRadius;            // starting radius
    private final double ringMaxRadius;              // maximum radius
    private final double ringExpandPerTick;          // radius increase per tick
    private final float damageMin;                   // damage on hit
    private final float damageMax;                   // damage on hit
    private final boolean applySlow;                 // apply slow on hit
    private final int slowTicks;                     // slow duration
    private final int slowPercent;                   // e.g. 30 = -30% speed
    private final boolean knockback;                 // apply outward knockback from bouquet center
    private final double knockbackStrength;          // kb magnitude

    // ---- Visuals / SFX ----
    private final Particle dashTrail = Particle.SPORE_BLOSSOM_AIR;
    private final Particle ringParticle = Particle.DUST;
    private final DustOptions ringDust = new DustOptions(Color.fromRGB(255, 105, 180), 1.05f);
    private final Particle bloomParticle = Particle.HEART;
    private final Particle popParticle = Particle.BLOCK_CRUMBLE;
    private final BlockData popBlock = Material.PINK_CONCRETE.createBlockData();
    private final Sound startSfx = Sound.BLOCK_AMETHYST_BLOCK_RESONATE;
    private final Sound dropSfx = Sound.BLOCK_AZALEA_PLACE;
    private final Sound ringTickSfx = Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES;

    public BouquetWaltzAbility(
            @Nonnull WarlordsNPC source,
            @Nonnull Supplier<Location> startSupplier,
            @Nonnull Supplier<Location> endSupplier,
            // dash/path
            int dashTicks,
            double arcHeight,
            int dropEveryTicks,
            boolean glideDuringDash,
            // ring/bouquet
            int bouquetLifetimeTicks,
            double ringStartRadius,
            double ringMaxRadius,
            double ringExpandPerTick,
            float damageMin,
            float damageMax,
            boolean applySlow,
            int slowTicks,
            int slowPercent,
            boolean knockback,
            double knockbackStrength
    ) {
        this.source = source;
        this.startSupplier = startSupplier;
        this.endSupplier = endSupplier;

        this.dashTicks = Math.max(10, dashTicks);
        this.arcHeight = Math.max(0.0, arcHeight);
        this.dropEveryTicks = Math.max(1, dropEveryTicks);
        this.glideDuringDash = glideDuringDash;

        this.bouquetLifetimeTicks = Math.max(20, bouquetLifetimeTicks);
        this.ringStartRadius = Math.max(0.25, ringStartRadius);
        this.ringMaxRadius = Math.max(this.ringStartRadius, ringMaxRadius);
        this.ringExpandPerTick = Math.max(0.05, ringExpandPerTick);
        this.damageMin = Math.max(0f, damageMin);
        this.damageMax = Math.max(this.damageMin, damageMax);
        this.applySlow = applySlow;
        this.slowTicks = Math.max(0, slowTicks);
        this.slowPercent = Math.min(Math.max(slowPercent, 0), 100);
        this.knockback = knockback;
        this.knockbackStrength = Math.max(0.0, knockbackStrength);
    }

    /**
     * Execute the dash and drop bouquets along the path.
     */
    public void cast() {
        final Location start = groundSnap(startSupplier.get().clone());
        final Location end = groundSnap(endSupplier.get().clone());
        if (start.getWorld() == null || end.getWorld() == null) return;

        // Control point for a smooth arc (simple quadratic bezier): use midpoint + arcHeight
        final Location mid = start.clone().add(end).multiply(0.5);
        double peakY = Math.max(start.getY(), end.getY()) + arcHeight;
        mid.setY(peakY);

        start.getWorld().playSound(start, startSfx, 2, 1.1f);

        new GameRunnable(source.getGame()) {
            int t = 0;
            @Override
            public void run() {
                double progress = (double) t / dashTicks;
                if (progress > 1.0) {
                    this.cancel();
                    return;
                }

                // Quadratic Bezier interpolation (start -> mid -> end)
                Location pos = bezier(start, mid, end, progress);
                // Move boss (glide illusion by teleporting smoothly)
                if (glideDuringDash) {
                    source.teleport(pos);
                } else {
                    // minor vertical smoothing if needed
                    source.teleport(pos);
                }

                // visuals
                pos.getWorld().spawnParticle(dashTrail, pos, 3, 0.1, 0.1, 0.1, 0.0);

                // Drop a bouquet node at interval
                if (t % dropEveryTicks == 0) {
                    dropBouquetNode(groundSnap(pos.clone()));
                    pos.getWorld().playSound(pos, dropSfx, 2, 1.2f);
                }

                t++;
            }
        }.runTaskTimer(0, 1);
    }

    // Spawns a bouquet node that emits an expanding ring, hitting each enemy at most once.
    private void dropBouquetNode(Location at) {
        final Set<UUID> hitOnce = new HashSet<>();
        final double maxR = ringMaxRadius;

        new GameRunnable(source.getGame()) {
            double r = ringStartRadius;
            int life = bouquetLifetimeTicks;

            @Override
            public void run() {
                if (life-- <= 0 || r > maxR) {
                    // small finish pop
                    at.getWorld().spawnParticle(bloomParticle, at, 10, 0.3, 0.15, 0.3, 0.0);
                    at.getWorld().spawnParticle(popParticle, at, 18, 0.5, 0.25, 0.5, 0.05, popBlock);
                    this.cancel();
                    return;
                }

                // ring visuals (approximate circle)
                drawRingDust(at, r, 24, ringDust);
                if (life % 6 == 0) {
                    at.getWorld().playSound(at, ringTickSfx, 2, 1.4f);
                }

                // Hit detection: entities within current radius that haven't been hit by THIS bouquet
                PlayerFilter.entitiesAround(at, r, 3, r)
                        .aliveEnemiesOf(source)
                        .forEach(wp -> {
                            UUID id = wp.getUuid();
                            if (!hitOnce.add(id)) return; // already hit by this bouquet

                            // Damage
                            wp.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Waltz")
                                    .source(source)
                                    .min(damageMin)
                                    .max(damageMax)
                            );

                            // Optional slow
                            if (applySlow && slowPercent > 0 && slowTicks > 0) {
                                wp.addSpeedModifier(source, "Bouquet Waltz Ring", -slowPercent, slowTicks);
                            }

                            // Optional knockback
                            if (knockback && knockbackStrength > 0) {
                                Utils.addKnockback("Lilium Knockback", source.getLocation(), wp, -1.15, knockbackStrength);
                            }
                        });

                r += ringExpandPerTick;
            }
        }.runTaskTimer(0, 1);
    }

    // --- helpers ---

    private static Location bezier(Location a, Location b, Location c, double t) {
        // Quadratic bezier lerp: (1-t)^2 * A + 2(1-t)t * B + t^2 * C
        double u = 1.0 - t;
        double uu = u * u;
        double tt = t * t;
        double x = uu * a.getX() + 2 * u * t * b.getX() + tt * c.getX();
        double y = uu * a.getY() + 2 * u * t * b.getY() + tt * c.getY();
        double z = uu * a.getZ() + 2 * u * t * b.getZ() + tt * c.getZ();
        return new Location(a.getWorld(), x, y, z);
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
