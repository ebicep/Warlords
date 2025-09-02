package com.ebicep.warlords.pve.mobs;

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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class MeteorMarkersAbility {

    private final WarlordsEntity caster;       // team/faction reference
    private final WarlordsNPC warlordsNPC;     // damage source
    private final Supplier<Location> centerSupplier;

    // Config
    private final double arenaRadius;          // circle radius where markers may spawn
    private final int markerCount;             // how many meteors per cast
    private final int telegraphTicks;          // delay before impact (e.g., 60 = 3s)
    private final double impactRadius;         // AoE radius of each meteor
    private final double damage;               // damage dealt at impact
    private final double knockUp;              // upward velocity on hit (0 = off)
    private final boolean followCenter;        // if true, markers move with center (usually false)
    private final double ringStep;             // particle spacing along ring arc (smaller = denser)
    private final int columnHeight;            // particle column height for impact VFX
    private final int lingerTicks;             // optional lingering hazard duration (0 = none)
    private final double lingerDamagePerTick;  // damage per tick during lingering
    private final Random rng = new Random();

    // Runtime
    private final List<Marker> markers = new ArrayList<>();
    private GameRunnable loop;
    private boolean active = false;

    public MeteorMarkersAbility(
            WarlordsEntity caster,
            WarlordsNPC warlordsNPC,
            Supplier<Location> centerSupplier,
            double arenaRadius,
            int markerCount,
            int telegraphTicks,
            double impactRadius,
            double damage,
            double knockUp,
            boolean followCenter,
            double ringStep,
            int columnHeight,
            int lingerTicks,
            double lingerDamagePerTick
    ) {
        this.caster = caster;
        this.warlordsNPC = warlordsNPC;
        this.centerSupplier = centerSupplier;

        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.markerCount = Math.max(1, markerCount);
        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.impactRadius = Math.max(0.5, impactRadius);
        this.damage = Math.max(0.0, damage);
        this.knockUp = Math.max(0.0, knockUp);
        this.followCenter = followCenter;
        this.ringStep = Math.max(0.2, ringStep);
        this.columnHeight = Math.max(1, columnHeight);
        this.lingerTicks = Math.max(0, lingerTicks);
        this.lingerDamagePerTick = Math.max(0.0, lingerDamagePerTick);
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (active) return;
        active = true;

        final Location center = safeCenter();
        final World w = center.getWorld();
        if (w == null) { stop(); return; }

        spawnMarkers(center);

        loop = new GameRunnable(game) {
            int t = 0;
            @Override
            public void run() {
                if (!active) { cancel(); return; }
                Location base = followCenter ? safeCenter() : center;
                World world = base.getWorld();
                if (world == null) { stop(); cancel(); return; }

                t++;

                // Telegraph: draw rings & ticking sparkles
                for (Marker m : markers) {
                    // Update position if following a moving center
                    if (followCenter) {
                        m.worldPos = worldPosFromPolar(base, m.r, m.a, m.y);
                    }

                    if (!m.impacted) {
                        drawTelegraphRing(world, m.worldPos, impactRadius, ringStep, t);
                        world.spawnParticle(Particle.ENCHANTED_HIT, m.worldPos, 2, 0.1, 0.0, 0.1, 0);
                    }

                    // Impact moment
                    if (!m.impacted && t >= m.impactAt) {
                        m.impacted = true;
                        impact(world, m.worldPos);
                        m.lingerUntil = (lingerTicks > 0) ? t + lingerTicks : -1;
                    }

                    // Lingering hazard
                    if (m.impacted && m.lingerUntil >= 0 && t <= m.lingerUntil) {
                        world.spawnParticle(Particle.SMOKE, m.worldPos.clone().add(0, 0.2, 0), 3, 0.15, 0.0, 0.15, 0.01);
                        if (lingerDamagePerTick > 0) {
                            for (WarlordsEntity enemy : PlayerFilter
                                    .entitiesAround(m.worldPos, impactRadius, 3, impactRadius)
                                    .aliveEnemiesOf(caster)
                            ) {
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Meteor Flames")
                                        .value((float) lingerDamagePerTick)
                                        .source(warlordsNPC)
                                );
                            }
                        }
                    }
                }

                // End when all meteors have finished lingering (or immediately after impacts if no linger)
                boolean anyActive = false;
                for (Marker m : markers) {
                    if (!m.impacted || (m.lingerUntil >= 0 && t <= m.lingerUntil)) {
                        anyActive = true;
                        break;
                    }
                }
                if (!anyActive) { stop(); cancel(); }
            }

            @Override
            public void cancel() {
                super.cancel(); // no recursive cleanup here
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
        markers.clear();
    }

    /* ================= Internals ================= */

    private void spawnMarkers(Location base) {
        World w = base.getWorld();
        if (w == null) return;

        // Sound cue
        w.playSound(base, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 500.0f, 0.8f);

        for (int i = 0; i < markerCount; i++) {
            // Random point inside circle (polar sampling with sqrt for uniform area)
            double u = rng.nextDouble();
            double r = Math.sqrt(u) * (arenaRadius - impactRadius * 0.5); // keep off the very edge
            double a = rng.nextDouble() * Math.PI * 2.0;

            Location pos = worldPosFromPolar(base, r, a, 0); // y offset 0 -> ground level (adjust if needed)

            Marker m = new Marker();
            m.r = r;
            m.a = a;
            m.y = 0.0;
            m.worldPos = pos;
            m.impactAt = telegraphTicks; // all at same time; stagger if you like by adding rng.nextInt(10)

            markers.add(m);

            // Initial brighter ring pulse so players notice the spot
            ringFlash(w, pos, impactRadius, 2, Color.fromRGB(255, 120, 80));
        }
    }

    private void impact(World w, Location at) {
        // Visuals + SFX
        w.playSound(at, Sound.ENTITY_GENERIC_EXPLODE, 2, 1.1f);
        w.spawnParticle(Particle.EXPLOSION, at, 2, 0, 0, 0, 0);
        for (int y = 0; y < columnHeight; y++) {
            Location p = at.clone().add(0, y * 0.5, 0);
            w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, p, 5, 0.15, 0.0, 0.15, 0.01);
            w.spawnParticle(Particle.LAVA, p, 5, 0.1, 0.1, 0.1, 0.0);
        }

        // Damage + optional knock-up
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(at, impactRadius, 3, impactRadius)
                .aliveEnemiesOf(caster)
        ) {
            enemy.addInstance(InstanceBuilder
                    .damage()
                    .cause("Meteor")
                    .value((float) damage)
                    .source(warlordsNPC)
            );

            if (knockUp > 0) {
                Utils.addKnockback("Meteor", at, enemy, -1, knockUp);
            }
        }
    }

    private void drawTelegraphRing(World w, Location center, double radius, double step, int tick) {
        // Animate ring brightness with a soft pulse
        float size = 1.0f + 0.2f * (float)Math.sin(tick * 0.3);
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(255, 180, 80), size);

        double twoPi = Math.PI * 2.0;
        double angStep = Math.max(0.02, step / Math.max(0.1, radius));
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            w.spawnParticle(Particle.DUST, new Location(w, x, center.getY(), z), 0, dust);
        }
    }

    private void ringFlash(World w, Location center, double radius, int pulses, Color color) {
        Particle.DustOptions dust = new Particle.DustOptions(color, 1.5f);
        double twoPi = Math.PI * 2.0;
        double angStep = Math.max(0.02, ringStep / Math.max(0.1, radius));
        for (int i = 0; i < pulses; i++) {
            for (double a = 0; a < twoPi; a += angStep) {
                double x = center.getX() + Math.cos(a) * radius;
                double z = center.getZ() + Math.sin(a) * radius;
                w.spawnParticle(Particle.DUST, new Location(w, x, center.getY(), z), 0, dust);
            }
        }
    }

    private static Location worldPosFromPolar(Location base, double r, double a, double yOffset) {
        return new Location(base.getWorld(),
                base.getX() + Math.cos(a) * r,
                base.getY() + yOffset,
                base.getZ() + Math.sin(a) * r
        );
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().get(0), 0, 0, 0) : c.clone();
    }

    /* ---------- Data ---------- */
    private static final class Marker {
        double r, a, y;
        Location worldPos;
        int impactAt;
        boolean impacted = false;
        int lingerUntil = -1;
    }
}