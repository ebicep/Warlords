package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.function.Supplier;

public class RotatingRadialLasersAbility {

    private final WarlordsNPC source;               // damage source
    private final Supplier<Location> centerSupplier; // usually () -> static arena center

    // Geometry / timing
    private final int beams;                // number of radial beams
    private final double radius;            // length of each beam (arena radius)
    private final float speedDegPerTick;    // rotation speed (degrees per tick, can be negative)
    private final int chargeTicks;          // harmless telegraph duration
    private final int fireTicks;            // damaging duration

    // Damage / hitbox
    private final double width;             // hit radius around each beam (cylinder half-width)
    private final double verticalHalf;      // vertical half-extent for AABB filter
    private final double dps;               // damage per tick when inside a beam

    // Visuals
    private final double step;              // particle spacing along a beam
    private final double beamY;             // beam height above center Y (e.g., 1.2)
    private final Particle.DustOptions telegraphDust;
    private final Particle.DustOptions fireDust;

    // Runtime
    private GameRunnable loop;
    private boolean running = false;
    private float baseAngleDeg = 0f;

    public RotatingRadialLasersAbility(
            WarlordsNPC source,
            Supplier<Location> centerSupplier,
            int beams,
            double radius,
            float speedDegPerTick,
            int chargeTicks,
            int fireTicks,
            double width,
            double verticalHalf,
            double dps,
            double step,
            double beamYOffset,
            Color telegraphColor,
            Color fireColor
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        this.beams = Math.max(1, beams);
        this.radius = Math.max(1.0, radius);
        this.speedDegPerTick = speedDegPerTick;
        this.chargeTicks = Math.max(1, chargeTicks);
        this.fireTicks = Math.max(1, fireTicks);
        this.width = Math.max(0.1, width);
        this.verticalHalf = Math.max(1.0, verticalHalf);
        this.dps = Math.max(0.0, dps);
        this.step = Math.max(0.2, step);
        this.beamY = beamYOffset;

        this.telegraphDust = new Particle.DustOptions(telegraphColor, 3f);
        this.fireDust = new Particle.DustOptions(fireColor, 4.5f);
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        baseAngleDeg = 0f;

        loop = new GameRunnable(game) {
            int t = 0;

            @Override public void run() {
                t++;

                Location center = safeCenter();
                World w = center.getWorld();
                if (w == null) { stop(); cancel(); return; }

                // Spin
                baseAngleDeg = (baseAngleDeg + speedDegPerTick) % 360f;
                boolean charging = t <= chargeTicks;
                boolean firing   = t > chargeTicks && t <= (chargeTicks + fireTicks);

                // Draw + (optionally) damage for each beam
                for (int i = 0; i < beams; i++) {
                    double theta = Math.toRadians(baseAngleDeg + (360.0 * i / beams));
                    double dx = Math.cos(theta);
                    double dz = Math.sin(theta);

                    // Beam line from center to tip at fixed Y
                    drawBeam(w, center, dx, dz, charging ? telegraphDust : fireDust, charging);

                    if (firing && dps > 0) {
                        applyDamageAlongBeam(center, dx, dz);
                    }
                }

                // Sounds (subtle)
                if (charging && t == 1) {
                    w.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 10, 1.25f);
                } else if (t == chargeTicks + 1) {
                    w.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 10, 0.95f);
                } else if (firing && t % 10 == 0) {
                    w.playSound(center, Sound.ENTITY_ENDER_EYE_DEATH, 10, 0.5f);
                    w.playSound(center, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 10, 0.5f);
                }

                // End
                if (t > chargeTicks + fireTicks) {
                    w.playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 10, 1.2f);
                    stop();
                    cancel();
                }
            }

            @Override public void cancel() {
                super.cancel(); // no recursive cleanup
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
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void drawBeam(World w, Location center, double dx, double dz, Particle.DustOptions dust, boolean isCharge) {
        final double y = center.getY() + beamY;
        for (double d = 0; d <= radius; d += step) {
            double x = center.getX() + dx * d;
            double z = center.getZ() + dz * d;

            // Use the "full" overload to ensure visibility
            if (isCharge) {
                w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
            } else {
                w.spawnParticle(Particle.SONIC_BOOM, x, y, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    private void applyDamageAlongBeam(Location center, double dx, double dz) {
        // Broad-phase: single AABB around the disc
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(center, radius + width + 1.0, verticalHalf, radius + width + 1.0)
                .aliveEnemiesOf(source)
        ) {
            Location p = enemy.getLocation();
            if (p.getWorld() != center.getWorld()) continue;

            // Project P-O onto the beam axis and clamp to [0, radius]
            double px = p.getX() - center.getX();
            double pz = p.getZ() - center.getZ();
            double proj = px * dx + pz * dz;                // signed distance along beam axis
            double t = Math.max(0, Math.min(radius, proj)); // clamp to segment

            double hx = t * dx; // closest point on beam (relative)
            double hz = t * dz;

            double distSq = (px - hx) * (px - hx) + (pz - hz) * (pz - hz);
            if (distSq <= width * width) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Radial Laser")
                        .value((float) dps)
                        .source(source)
                );
            }
        }
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().get(0), 0, 64, 0) : c.clone();
    }
}
