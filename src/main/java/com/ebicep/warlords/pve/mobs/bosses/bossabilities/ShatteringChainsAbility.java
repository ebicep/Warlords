package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class ShatteringChainsAbility {

    private final WarlordsEntity source;                 // boss at (center)
    private final Supplier<Location> centerSupplier;  // () -> arena center (flat floor Y)

    // Geometry & timing
    private final int chainCount;           // number of chain lines
    private final double arenaRadius;       // arena radius (center -> edge)
    private final int telegraphTicks;       // warning duration (safe)
    private final int activeTicks;          // hazard duration (damaging)
    private final int damageInterval;       // ticks between damage applications
    private final double bandHalfWidth;     // hit half-width around the chain line
    private final double verticalHalf;      // Y half-height for AABB broad-phase
    private final double step;              // particle spacing along chains

    // Damage
    private final double damagePerHit;      // damage applied each interval while inside band

    // Visuals
    private final Particle.DustOptions telegraphDust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 2f);
    private final Particle.DustOptions activeDust    = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 3f);

    // Runtime
    private final List<Chain> chains = new ArrayList<>();
    private GameRunnable loop;
    private boolean running = false;

    private final Random rng = new Random();

    public ShatteringChainsAbility(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            int chainCount,
            double arenaRadius,
            int telegraphTicks,
            int activeTicks,
            int damageInterval,
            double bandHalfWidth,
            double verticalHalf,
            double step,
            double damagePerHit
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;
        this.chainCount = Math.max(1, chainCount);
        this.arenaRadius = Math.max(2.0, arenaRadius);
        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.activeTicks = Math.max(1, activeTicks);
        this.damageInterval = Math.max(1, damageInterval);
        this.bandHalfWidth = Math.max(0.2, bandHalfWidth);
        this.verticalHalf = Math.max(1.0, verticalHalf);
        this.step = Math.max(0.25, step);
        this.damagePerHit = Math.max(0.0, damagePerHit);
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        chains.clear();

        final Location center = safeCenter();
        final World w = center.getWorld();
        if (w == null) { stop(); return; }

        // Pick random anchor angles on the perimeter (shuffle for variety)
        List<Double> angles = new ArrayList<>();
        for (int i = 0; i < chainCount; i++) angles.add(rng.nextDouble() * Math.PI * 2);
        Collections.sort(angles); // optional: sorted looks tidy; remove if you want fully random

        for (double ang : angles) {
            Location anchor = new Location(
                    w,
                    center.getX() + Math.cos(ang) * arenaRadius,
                    center.getY() + 1,
                    center.getZ() + Math.sin(ang) * arenaRadius
            );
            chains.add(new Chain(ang, anchor));
        }

        Utils.playGlobalSound(center, Sound.ENTITY_ZOGLIN_DEATH, 500, 0.5f);

        loop = new GameRunnable(game) {
            int tick = 0;
            int lastDamageTick = -9999;

            @Override
            public void run() {
                tick++;

                Location c = safeCenter();
                World world = c.getWorld();
                if (world == null) { stop(); cancel(); return; }

                boolean telegraph = tick <= telegraphTicks;
                boolean active = tick > telegraphTicks && tick <= (telegraphTicks + activeTicks);

                // Draw
                for (Chain ch : chains) {
                    drawChain(world, c, ch.anchor, telegraph ? telegraphDust : activeDust, telegraph);
                }

                // Tighten moment
                if (tick == telegraphTicks + 1) {
                    Utils.playGlobalSound(c, Sound.BLOCK_CHAIN_BREAK, 200, 0.5f);
                    Utils.playGlobalSound(c, "rogue.hearttoheart.activation", 200, 0.5f);
                    Utils.playGlobalSound(c, "rogue.hearttoheart.activation.alt", 200, 0.5f);
                    world.spawnParticle(Particle.SONIC_BOOM, c, 10, 0.5,0.5,.5, 0.0);
                }

                // Damage while active (interval)
                if (active && damagePerHit > 0 && tick - lastDamageTick >= damageInterval) {
                    lastDamageTick = tick;
                    applyDamageBands(c);
                    if ((tick % 10) == 0) {
                        Utils.playGlobalSound(c, Sound.ITEM_AXE_WAX_OFF, 200, 0.5f);
                    }
                }

                // End
                if (tick > telegraphTicks + activeTicks) {
                    // shatter VFX along anchors
                    for (Chain ch : chains) {
                        world.spawnParticle(Particle.EXPLOSION, ch.anchor, 2, 0,0,0, 0.0);
                    }
                    Utils.playGlobalSound(c, Sound.BLOCK_CHAIN_PLACE, 200, 0.5f);
                    Utils.playGlobalSound(c, "rogue.hearttoheart.activation", 200, 0.5f);
                    Utils.playGlobalSound(c, "rogue.hearttoheart.activation.alt", 200, 0.5f);
                    stop();
                    cancel();
                }
            }
        };
        loop.runTaskTimer(0, 1);
    }

    public void stop() {
        running = false;
        if (loop != null) {
            GameRunnable r = loop;
            loop = null;
            r.cancel();
        }
        chains.clear();
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void drawChain(World w, Location center, Location anchor, Particle.DustOptions dust, boolean telegraphLook) {
        // draw along the segment center -> anchor at a fixed Y
        double y = center.getY() + 0.05;
        Vector dir = anchor.toVector().subtract(center.toVector());
        dir.setY(0);
        double len = dir.length();
        if (len < 1e-6) return;

        Vector stepVec = dir.clone().normalize().multiply(step);
        int samples = Math.max(1, (int) Math.ceil(len / step));

        Location p = new Location(w, center.getX(), y, center.getZ());
        for (int i = 0; i <= samples; i++) {
            w.spawnParticle(Particle.DUST, p, 1, 0,0,0, 0.0, dust);
            if (!telegraphLook && (i % 6) == 0) {
                w.spawnParticle(Particle.ELECTRIC_SPARK, p, 1, 0,0,0, 0.0);
            }
            p.add(stepVec);
        }
    }

    private void applyDamageBands(Location center) {
        // Broad-phase: grab entities in a disc slightly larger than arena
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(center, arenaRadius + 2.0, verticalHalf, arenaRadius + 2.0)
                .aliveEnemiesOf(source)
        ) {
            Location p = enemy.getLocation();
            if (p.getWorld() != center.getWorld()) continue;

            // Check against each chain segment; if within band, apply damage once
            if (touchesAnyBand(center, p)) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Shattering Chains")
                        .value((float) damagePerHit)
                        .source(source)
                );
            }
        }
    }

    private boolean touchesAnyBand(Location center, Location point) {
        double px = point.getX() - center.getX();
        double pz = point.getZ() - center.getZ();

        for (Chain ch : chains) {
            // Segment is center -> anchor (in XZ)
            double ax = ch.anchor.getX() - center.getX();
            double az = ch.anchor.getZ() - center.getZ();
            double segLenSq = ax*ax + az*az;
            if (segLenSq < 1e-6) continue;

            // projection of P onto segment, clamp to [0,1]
            double t = (px*ax + pz*az) / segLenSq;
            if (t < 0) t = 0;
            if (t > 1) t = 1;

            double cx = ax * t; // closest point (relative)
            double cz = az * t;

            double dx = px - cx;
            double dz = pz - cz;
            double distSq = dx*dx + dz*dz;

            if (distSq <= bandHalfWidth * bandHalfWidth) {
                return true;
            }
        }
        return false;
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().get(0), 0, 64, 0) : c.clone();
    }

    /* ---------------- Data ---------------- */
    private static final class Chain {
        final double angle;
        final Location anchor;
        Chain(double angle, Location anchor) {
            this.angle = angle;
            this.anchor = anchor;
        }
    }
}
