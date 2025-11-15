package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConvergingShockwavesAbility {

    private final WarlordsEntity source;                 // your boss (also used for aliveEnemiesOf)
    private final Supplier<Location> centerSupplier;  // usually () -> arenaCenter.clone()

    // Geometry & timing
    private final double arenaRadius;     // arena size (blocks)
    private final int waveCount;          // number of waves to send
    private final int waveIntervalTicks;  // time between wave spawns
    private final int telegraphTicks;     // per-wave telegraph before moving
    private final double speed;           // inward speed (blocks/tick)
    private final double thickness;       // hit half-width around the ring
    private final double verticalHalf;    // Y half-height for AABB checks

    // Damage
    private final double damagePerTick;   // applied per tick while inside the ring band

    // Visuals
    private final double ringStep;        // arc sampling (smaller = denser)
    private final double yOffset;         // draw height relative to center Y (0.05 sits just above floor)
    private final Particle.DustOptions telegraphDust;
    private final Particle.DustOptions shockDust;

    // Runtime
    private final List<Wave> waves = new ArrayList<>();
    private GameRunnable loop;
    private boolean running = false;

    public ConvergingShockwavesAbility(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            double arenaRadius,
            int waveCount,
            int waveIntervalTicks,
            int telegraphTicks,
            double speed,
            double thickness,
            double verticalHalf,
            double damagePerTick,
            double ringStep,
            double yOffset,
            Color telegraphColor,
            Color shockColor
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.waveCount = Math.max(1, waveCount);
        this.waveIntervalTicks = Math.max(1, waveIntervalTicks);
        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.speed = Math.max(0.01, speed);
        this.thickness = Math.max(0.1, thickness);
        this.verticalHalf = Math.max(1.0, verticalHalf);

        this.damagePerTick = Math.max(0.0, damagePerTick);

        this.ringStep = Math.max(0.2, ringStep);
        this.yOffset = yOffset;

        this.telegraphDust = new Particle.DustOptions(telegraphColor, 1.2f);
        this.shockDust = new Particle.DustOptions(shockColor, 1.5f);
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        waves.clear();

        // Pre-schedule waves (each wave knows when it starts)
        int startTick = 0;
        for (int i = 0; i < waveCount; i++) {
            waves.add(new Wave(startTick, arenaRadius));
            startTick += waveIntervalTicks;
        }

        loop = new GameRunnable(game) {
            int t = 0;

            @Override
            public void run() {
                t++;

                Location center = safeCenter();
                World w = center.getWorld();
                if (w == null) { stop(); cancel(); return; }

                boolean anyAlive = false;

                for (Wave wave : waves) {
                    if (wave.done) continue;

                    int age = t - wave.spawnTick;
                    if (age < 0) { // not started yet
                        anyAlive = true;
                        continue;
                    }

                    // Telegraph phase
                    if (age <= telegraphTicks) {
                        anyAlive = true;
                        drawRing(w, center, wave.radius, center.getY() + yOffset, telegraphDust);
                        if (age == 1) {
                            w.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 10, 1.2f);
                        }
                        continue;
                    }

                    // Move inward
                    wave.radius = Math.max(0, wave.radius - speed);
                    if (wave.radius <= 0) {
                        wave.done = true;
                        EffectUtils.strikeLightningInCylinder(center, 6, false);
                        continue;
                    }

                    anyAlive = true;

                    // Draw shockwave ring
                    drawRing(w, center, wave.radius, center.getY() + yOffset, shockDust);

                    // Apply damage band around this radius
                    if (damagePerTick > 0) {
                        damageOnRing(center, wave.radius);
                    }

                    // Optional subtle hum
                    if (t % 8 == 0) {
                        w.playSound(center, Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 1.8f);
                    }
                }

                if (!anyAlive) {
                    w.playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 10, 1.2f);
                    stop();
                    cancel();
                }
            }

            @Override
            public void cancel() {
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
        waves.clear();
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void drawRing(World w, Location center, double radius, double y, Particle.DustOptions dust) {
        if (radius <= 0) return;
        // convert arc length step to angle step
        final double angStep = Math.max(0.02, ringStep / Math.max(0.1, radius));
        final double twoPi = Math.PI * 2;
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            // full overload: count=1, no spread, extra=0, with DustOptions
            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private void damageOnRing(Location center, double radius) {
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(center, radius + thickness + 1.0, verticalHalf, radius + thickness + 1.0)
                .aliveEnemiesOf(source)
        ) {
            Location p = enemy.getLocation();
            if (p.getWorld() != center.getWorld()) continue;

            double dx = p.getX() - center.getX();
            double dz = p.getZ() - center.getZ();
            double dist = Math.sqrt(dx*dx + dz*dz);

            if (Math.abs(dist - radius) <= thickness) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Converging Shockwave")
                        .value((float) damagePerTick)
                        .source(source)
                );
            }
        }
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().getFirst(), 0, 64, 0) : c.clone();
    }

    /* ---------------- Data ---------------- */

    private static final class Wave {
        final int spawnTick;   // global tick when this wave starts telegraphing
        double radius;         // current radius
        boolean done = false;

        Wave(int spawnTick, double startRadius) {
            this.spawnTick = spawnTick;
            this.radius = startRadius;
        }
    }
}
