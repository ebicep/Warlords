package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.function.Supplier;

public class ArenaCollapseAbility {

    private final WarlordsEntity caster;       // who owns this ability (faction checks)
    private final WarlordsNPC warlordsNPC;     // damage source for InstanceBuilder
    private final Supplier<Location> centerSupplier; // center can be static OR follow an entity via supplier

    // Config
    private final double initialRadius;        // starting safe radius (blocks)
    private final double minRadius;            // stop shrinking at this radius
    private final double shrinkAmount;         // how much radius to remove per shrink
    private final int shrinkIntervalTicks;     // how often to shrink (ticks)
    private final double damagePerTick;        // damage applied each tick outside
    private final double nudgeStrength;        // optional inward push (0 = off)
    private final double checkHeight;          // Y-range for PlayerFilter (half-height)
    private final double ringDensity;          // particle step in blocks along circumference

    // Runtime
    private GameRunnable loop;
    private boolean active = false;
    private boolean paused = false;
    private double currentRadius;
    private int ticks = 0;

    /**
     * @param centerSupplier  center of arena (use () -> staticLocation.clone() or () -> boss.getLocation())
     * @param initialRadius   starting safe-zone radius
     * @param minRadius       minimum radius (won’t shrink below this)
     * @param shrinkAmount    radius reduction each interval
     * @param shrinkIntervalTicks ticks between shrink steps
     * @param damagePerTick   damage dealt each tick to enemies outside
     * @param nudgeStrength   inward push strength (0 to disable)
     * @param checkHeight     Y half-extent for PlayerFilter (e.g. 4 means +/-4 Y)
     * @param ringDensity     particle spacing along the circle (smaller = denser ring)
     */
    public ArenaCollapseAbility(
            WarlordsEntity caster,
            WarlordsNPC warlordsNPC,
            Supplier<Location> centerSupplier,
            double initialRadius,
            double minRadius,
            double shrinkAmount,
            int shrinkIntervalTicks,
            double damagePerTick,
            double nudgeStrength,
            double checkHeight,
            double ringDensity
    ) {
        this.caster = caster;
        this.warlordsNPC = warlordsNPC;
        this.centerSupplier = centerSupplier;

        this.initialRadius = initialRadius;
        this.minRadius = Math.max(0.1, minRadius);
        this.shrinkAmount = Math.max(0.0, shrinkAmount);
        this.shrinkIntervalTicks = Math.max(1, shrinkIntervalTicks);
        this.damagePerTick = Math.max(0.0, damagePerTick);
        this.nudgeStrength = Math.max(0.0, nudgeStrength);
        this.checkHeight = Math.max(1.0, checkHeight);
        this.ringDensity = Math.max(0.15, ringDensity);

        this.currentRadius = initialRadius;
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (active) return;
        active = true;
        ticks = 0;
        currentRadius = initialRadius;

        loop = new GameRunnable(game) {
            @Override
            public void run() {
                if (!active) { cancel(); return; }
                final Location center = safeCenter();
                final World w = center.getWorld();
                if (w == null) { stop(); cancel(); return; }

                if (!paused) {
                    ticks++;

                    // Shrink step
                    if (shrinkAmount > 0 && ticks % shrinkIntervalTicks == 0 && currentRadius > minRadius + 1e-6) {
                        currentRadius = Math.max(minRadius, currentRadius - shrinkAmount);
                        // feedback
                        Utils.playGlobalSound(center, Sound.BLOCK_ANVIL_FALL, 2f, 1.2f);
                        ringFlash(w, center, currentRadius, 2, Color.fromRGB(255, 80, 80));
                    }
                }

                // Always draw the current safe ring (telegraph)
                drawRing(w, center, currentRadius, ringDensity, new Particle.DustOptions(Color.fromRGB(80, 180, 255), 1.1f));

                // Damage / nudge entities outside the ring (skip if paused? usually still applies)
                for (WarlordsEntity enemy : PlayerFilter
                        .entitiesAround(center, currentRadius + 6, checkHeight, currentRadius + 6)
                        .aliveEnemiesOf(caster)
                ) {
                    Location p = enemy.getLocation(); // WarlordsEntity loc
                    if (p == null || p.getWorld() != w) continue;

                    double dx = p.getX() - center.getX();
                    double dz = p.getZ() - center.getZ();
                    double distSq = dx*dx + dz*dz;
                    double rSq = currentRadius * currentRadius;

                    if (distSq > rSq) {
                        enemy.addInstance(InstanceBuilder
                                .damage()
                                .cause("Arena Collapse")
                                .value((float) damagePerTick)
                                .source(warlordsNPC)
                        );

                        // Optional: nudge inward
                        if (nudgeStrength > 0) {
                            Utils.addKnockback("Center", center, enemy, nudgeStrength, 0);
                        }

                        // Minor hit feedback on boundary (optional)
                        w.spawnParticle(Particle.CRIT, clampToRing(center, p, currentRadius), 1, 0.02, 0.02, 0.02, 0.0);
                    }
                }
            }

            @Override
            public void cancel() {
                super.cancel();
                // do not call ArenaCollapseAbility.stop() from here to avoid recursive cancel chains
            }
        };

        loop.runTaskTimer(0, 1);
    }

    public void stop() {
        active = false;
        if (loop != null) {
            GameRunnable r = loop;
            loop = null;
            r.cancel();
        }
    }

    public void pause() { paused = true; }
    public void resume() { paused = false; }

    public boolean isActive() { return active; }
    public double getCurrentRadius() { return currentRadius; }

    /** Force a one-time shrink now (e.g., on phase change). */
    public void forceShrink(double amount) {
        currentRadius = Math.max(minRadius, currentRadius - Math.max(0, amount));
    }

    /** Force set current radius (clamped). */
    public void setRadius(double radius) {
        currentRadius = Math.max(minRadius, Math.min(initialRadius, radius));
    }

    /* ================= Internals ================= */

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().getFirst(), 0, 0, 0) : c.clone();
    }

    private void drawRing(World w, Location center, double radius, double step, Particle.DustOptions dust) {
        if (radius <= 0) return;
        double twoPi = Math.PI * 2.0;
        // step is arc-length; convert to angle step = step / radius
        double angStep = Math.max(0.02, step / Math.max(0.1, radius));
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            Location p = new Location(w, x, center.getY(), z);
            w.spawnParticle(Particle.DUST, p, 0, dust);
        }
    }

    private void ringFlash(World w, Location center, double radius, int pulses, Color color) {
        Particle.DustOptions dust = new Particle.DustOptions(color, 1.5f);
        for (int i = 0; i < pulses; i++) {
            drawRing(w, center, radius, ringDensity * 0.8, dust);
        }
    }

    private Location clampToRing(Location center, Location point, double radius) {
        double dx = point.getX() - center.getX();
        double dz = point.getZ() - center.getZ();
        double len = Math.sqrt(dx*dx + dz*dz);
        if (len < 1e-6) return center.clone();
        double scale = radius / len;
        return new Location(center.getWorld(), center.getX() + dx * scale, point.getY(), center.getZ() + dz * scale);
    }
}
