package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AbilityLaserBarrage {

    private final Game game;
    private final Location center;
    private final int laserCount;
    private final int trackTicks;         // how long to track players
    private final int lockTelegraphTicks; // how long to show locked telegraph
    private final int fireTicks;          // how long to show the fire beam
    private final double range;
    private final double step;
    private final WarlordsEntity warlordsEntity;

    private final List<Laser> lasers = new ArrayList<>();
    private GameRunnable loop;

    public AbilityLaserBarrage(Game game,
                               Location center,
                               int laserCount,
                               int trackTicks,
                               int lockTelegraphTicks,
                               int fireTicks,
                               double range,
                               double step,
                               WarlordsEntity warlordsEntity) {
        this.game = game;
        this.center = center.clone();
        this.laserCount = laserCount;
        this.trackTicks = trackTicks;
        this.lockTelegraphTicks = lockTelegraphTicks;
        this.fireTicks = fireTicks;
        this.range = range;
        this.step = step;
        this.warlordsEntity = warlordsEntity;
    }

    /* ============ Public API ============ */

    public void start(Collection<? extends WarlordsEntity> candidates) {
        spawnLasers(candidates);
        runLoop();
    }

    private boolean closed = false;

    private void finish() {
        if (closed) return;
        closed = true;
        lasers.clear();
    }

    public void cancel() {
        finish();
        if (loop != null) {
            GameRunnable r = loop;
            loop = null;
            r.cancel();
        }
    }

    /* ============ Internals ============ */

    private void spawnLasers(Collection<? extends WarlordsEntity> candidates) {
        List<WarlordsEntity> pool = new ArrayList<>(candidates);
        pool.removeIf(p -> p == null || !p.isOnline() || p.isDead() || p.getWorld() != center.getWorld());

        ThreadLocalRandom rng = ThreadLocalRandom.current();

        for (int i = 0; i < laserCount; i++) {
            WarlordsEntity target = pool.isEmpty() ? null : pool.get(rng.nextInt(pool.size()));
            Vector initialDir;
            if (target != null) {
                initialDir = dirTo(center, target.getLocation().add(0, 1.2, 0));
            } else {
                double yawRad = Math.toRadians(rng.nextDouble(0, 360));
                initialDir = new Vector(Math.cos(yawRad), 0, Math.sin(yawRad));
            }
            lasers.add(new Laser(target, initialDir));
        }
    }

    private void runLoop() {
        loop = new GameRunnable(game) {
            int t = 0;
            @Override public void run() {
                t++;

                boolean trackingPhase = t <= trackTicks;
                boolean lockPhase = t > trackTicks && t <= trackTicks + lockTelegraphTicks;
                boolean firingPhase = t > trackTicks + lockTelegraphTicks && t <= trackTicks + lockTelegraphTicks + fireTicks;

                for (Laser L : lasers) {
                    if (L.done) continue;

                    if (trackingPhase) {
                        tickTracking(L);
                        drawBeam(L, Particle.DUST, Color.AQUA, 1.2f); // faint line
                    } else if (lockPhase) {
                        if (!L.locked) lockTarget(L);
                        drawBeam(L, Particle.DUST, Color.RED, 1.6f); // locked telegraph
                    } else if (firingPhase) {
                        if (!L.locked) lockTarget(L);
                        drawBeam(L, Particle.SONIC_BOOM, null, 0f); // fire beam
                        if (t % 10 == 0) {
                            Utils.playGlobalSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 500, 0.7f);
                        }

                        Vector dir = (L.lockedDir != null ? L.lockedDir : L.currentDir);
                        if (dir == null || dir.lengthSquared() < 1e-6) continue;
                        dir = dir.clone().normalize();

                        final double hitRadius = 1.1;
                        final double hitYHalf  = 2.5;
                        final double sample = Math.max(0.3, hitRadius * 0.5);

                        java.util.HashSet<java.util.UUID> hitThisTick = new java.util.HashSet<>();

                        World w = center.getWorld();
                        for (double d = 0; d <= range; d += sample) {
                            Location point = center.clone().add(dir.clone().multiply(d));
                            if (point.getWorld() != w) continue;

                            for (WarlordsEntity enemy : PlayerFilter
                                    .entitiesAround(point, hitRadius, hitYHalf, hitRadius)
                                    .aliveEnemiesOf(warlordsEntity)
                            ) {
                                var uuid = enemy.getUuid();
                                if (!hitThisTick.add(uuid)) continue;

                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Death Ray")
                                        .value(1000)
                                        .source(warlordsEntity)
                                );
                            }
                        }
                    } else {
                        L.done = true;
                    }
                }

                if (t > trackTicks + lockTelegraphTicks + fireTicks) {
                    finish();
                    super.cancel();
                }
            }

            @Override
            public void cancel() {
                finish();
                super.cancel();
            }
        };
        loop.runTaskTimer(0, 1);
    }

    /* ============ Phase helpers ============ */

    private void tickTracking(Laser L) {
        if (L.target != null && L.target.isOnline() && !L.target.isDead() && L.target.getWorld() == center.getWorld()) {
            Location aimAt = L.target.getLocation().add(0, 1.2, 0);
            L.currentDir = dirTo(center, aimAt);
        }
        L.tipPoint = center.clone().add(L.currentDir.clone().multiply(range));
    }

    private void lockTarget(Laser L) {
        L.locked = true;
        Vector locked = L.currentDir.clone();
        if (L.target != null && L.target.isOnline() && !L.target.isDead() && L.target.getWorld() == center.getWorld()) {
            locked = dirTo(center, L.target.getLocation().add(0, 1.2, 0));
        }
        L.lockedDir = locked.normalize();
        L.tipPoint = center.clone().add(L.lockedDir.clone().multiply(range));
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 500, 0.9f);
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 500, 0.9f);
    }

    private void drawBeam(Laser L, Particle type, Color dustColor, float size) {
        Vector dir = (L.locked ? L.lockedDir : L.currentDir);
        if (dir == null) return;

        World w = center.getWorld();
        Vector unit = dir.clone().normalize();
        for (double d = 0; d <= range; d += step) {
            Location p = center.clone().add(unit.clone().multiply(d));
            if (type == Particle.DUST && dustColor != null) {
                Particle.DustOptions dust = new Particle.DustOptions(dustColor, size);
                w.spawnParticle(type, p, 0, dust);
            } else {
                w.spawnParticle(type, p, 0, 0, 0, 0, 0.0);
            }
        }
    }

    /* ============ Utils ============ */

    private static Vector dirTo(Location from, Location to) {
        return to.toVector().subtract(from.toVector()).normalize();
    }

    /* ============ Data ============ */

    private static final class Laser {
        final WarlordsEntity target;
        Vector currentDir;
        Vector lockedDir;
        boolean locked = false;
        boolean done = false;
        Location tipPoint;

        Laser(WarlordsEntity target, Vector initialDir) {
            this.target = target;
            this.currentDir = initialDir.clone();
        }
    }
}
