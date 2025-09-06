package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Supplier;

public class GiantLaserAbility {

    private final WarlordsEntity caster;        // team filtering
    private final WarlordsNPC source;           // damage source for InstanceBuilder
    private final Supplier<Location> originSupplier; // usually () -> boss.getEyeLocation()

    // Config
    private final int chargeTicks;              // how long to telegraph before firing
    private final int fireTicks;                // how long the beam deals damage
    private final double range;                 // max distance of the beam
    private final double width;                 // hit radius around the beam line
    private final double step;                  // sampling step (visuals & hit)
    private final double damagePerTick;         // damage each tick while a target is inside the beam
    private final boolean clampToBlocks;        // stop at first block hit
    private final double verticalHalf;          // vertical half-height for nearby filter

    // Visuals
    private final Particle.DustOptions telegraphDust = new Particle.DustOptions(Color.fromRGB(120, 200, 255), 3.5f);
    private final Particle.DustOptions fireDust      = new Particle.DustOptions(Color.fromRGB(255, 160, 120), 6f);

    // Runtime
    private GameRunnable loop;
    private boolean running = false;
    private Vector lockedDir;    // direction locked at fire start
    private double lockedRange;  // clamped range (ray-traced) at fire start

    public GiantLaserAbility(
            WarlordsEntity caster,
            WarlordsNPC source,
            Supplier<Location> originSupplier,
            int chargeTicks,
            int fireTicks,
            double range,
            double width,
            double step,
            double damagePerTick,
            boolean clampToBlocks,
            double verticalHalf
    ) {
        this.caster = caster;
        this.source = source;
        this.originSupplier = originSupplier;
        this.chargeTicks = Math.max(1, chargeTicks);
        this.fireTicks = Math.max(1, fireTicks);
        this.range = Math.max(1.0, range);
        this.width = Math.max(0.1, width);
        this.step = Math.max(0.2, step);
        this.damagePerTick = Math.max(0.0, damagePerTick);
        this.clampToBlocks = clampToBlocks;
        this.verticalHalf = Math.max(1.0, verticalHalf);
    }

    /* ---------------- Public API ---------------- */

    public void start(Game game) {
        if (running) return;
        running = true;
        lockedDir = null;
        lockedRange = range;

        loop = new GameRunnable(game) {
            int t = 0;

            @Override
            public void run() {
                t++;
                Location origin = safeOrigin();
                World w = origin.getWorld();
                if (w == null) { stop(); cancel(); return; }

                if (t <= chargeTicks) {
                    Vector target = lookDirection(origin);
                    if (lockedDir == null) {
                        lockedDir = target.clone();
                    }

                    double smoothing = 0.2; // smaller = slower tracking, bigger = snappier
                    lockedDir = lockedDir.clone().multiply(1 - smoothing).add(target.clone().multiply(smoothing)).normalize();
                    double len = clampToBlocks ? clampRange(w, origin, lockedDir, range) : range;

                    drawBeam(w, origin, lockedDir, len, telegraphDust, false);

                    if (t == chargeTicks) {
                        lockedRange = len;
                        w.playSound(origin, Sound.BLOCK_BEACON_ACTIVATE, 10f, 0.9f);
                    }
                    return;
                }

                // Ensure locked
                if (lockedDir == null) {
                    lockedDir = lookDirection(origin);
                    lockedRange = clampToBlocks ? clampRange(w, origin, lockedDir, range) : range;
                }

                int fireT = t - chargeTicks;
                if (fireT <= fireTicks) {
                    drawBeam(w, origin, lockedDir, lockedRange, fireDust,  true);

                    if (damagePerTick > 0) {
                        applyDamageAlongBeam(origin, lockedDir, lockedRange);
                    }

                    if (fireT % 10 == 1) {
                        Utils.playGlobalSound(origin, Sound.ENTITY_GUARDIAN_ATTACK, 10, 1.1f);
                        Utils.playGlobalSound(origin, Sound.ENTITY_WARDEN_SONIC_BOOM, 10, 0.5f);
                    }
                    return;
                }

                // END
                Utils.playGlobalSound(origin, Sound.BLOCK_BEACON_DEACTIVATE, 10, 1.2f);
                stop();
                cancel();
            }

            @Override
            public void cancel() {
                super.cancel();
                // no recursive cleanup here
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

    /* ---------------- Internals ---------------- */

    private void drawBeam(World w, Location origin, Vector dir, double len, Particle.DustOptions dust, boolean electric) {
        Vector u = dir.clone().normalize();
        for (double d = 0; d <= len; d += step) {
            double x = origin.getX() + u.getX() * d;
            double y = origin.getY() + u.getY() * d;
            double z = origin.getZ() + u.getZ() * d;

            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);

            // “electric” accent
            if (electric) {
                w.spawnParticle(Particle.ELECTRIC_SPARK, x, y, z, 2, 0, 0, 0, 0.0);
            }
        }
        // endpoint highlight
        Location tip = origin.clone().add(u.multiply(len));
        w.spawnParticle(Particle.END_ROD, tip, 2, 0.03, 0.03, 0.03, 0.0);
    }

    private void applyDamageAlongBeam(Location origin, Vector dir, double len) {
        World w = origin.getWorld();
        Vector u = dir.clone().normalize();

        // Broad-phase query once (AABB around the whole segment)
        double r = len + 1.0;
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(origin, r, verticalHalf, r)
                .aliveEnemiesOf(caster)
        ) {
            Location p = enemy.getLocation();
            if (p.getWorld() != w) continue;

            // Distance from entity center to the beam segment
            Vector A = origin.toVector();
            Vector B = origin.clone().add(u.clone().multiply(len)).toVector();
            Vector P = p.add(0, enemy.getEntity().getHeight() * 0.5, 0).toVector(); // center mass

            double dist = distancePointToSegment(P, A, B);
            if (dist <= width) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Fulcrum Laser")
                        .value((float) damagePerTick)
                        .source(source)
                );
            }
        }
    }

    private static double distancePointToSegment(Vector p, Vector a, Vector b) {
        Vector ab = b.clone().subtract(a);
        double ab2 = ab.lengthSquared();
        if (ab2 < 1e-9) return p.clone().subtract(a).length();
        double t = p.clone().subtract(a).dot(ab) / ab2;
        t = Math.max(0, Math.min(1, t));
        Vector proj = a.clone().add(ab.multiply(t));
        return p.clone().subtract(proj).length();
    }

    private double clampRange(World w, Location origin, Vector dir, double maxRange) {
        RayTraceResult hit = w.rayTraceBlocks(origin, dir, maxRange, FluidCollisionMode.NEVER, true);
        if (hit != null) {
            hit.getHitPosition();
            return hit.getHitPosition().toLocation(w).distance(origin);
        }
        return maxRange;
    }

    private Vector lookDirection(Location origin) {
        Vector d = origin.getDirection();
        if (d.lengthSquared() < 1e-6) d = new Vector(1, 0, 0);
        return d.normalize();
    }

    private Location safeOrigin() {
        Location o = originSupplier.get();
        return (o == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 64, 0) : o.clone();
    }
}
