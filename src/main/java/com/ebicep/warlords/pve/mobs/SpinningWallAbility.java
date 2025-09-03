package com.ebicep.warlords.pve.mobs;

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
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.function.Supplier;

public class SpinningWallAbility {

    private final WarlordsEntity caster;       // for aliveEnemiesOf()
    private final WarlordsNPC warlordsNPC;     // damage source
    private final Supplier<Location> centerSupplier;

    // Config
    private final double arenaRadius;          // radius of circular arena (blocks)
    private final float  speedDegPerTick;      // how fast the wall rotates
    private final int    durationTicks;        // total lifetime
    private final double thickness;            // hit half-width (blocks) around the diameter
    private final double sampleStep;           // spacing between particle samples along the line
    private final boolean doDamage;            // toggle damage on/off
    private final double damagePerTick;        // damage applied when inside thickness
    private final double hitYHalf;             // vertical half-extent for filtering (Y)

    // Visuals
    private final Particle telegraphParticle;
    private final Particle.DustOptions dust;
    private final double floorYOffset;         // slight lift to avoid z-fighting (e.g., 0.01)

    // Runtime
    private GameRunnable task;
    private int ticks = 0;
    private float angleDeg = 0f;
    private boolean running = false;

    public SpinningWallAbility(
            WarlordsEntity caster,
            WarlordsNPC warlordsNPC,
            Supplier<Location> centerSupplier,
            double arenaRadius,
            float speedDegPerTick,
            int durationTicks,
            double thickness,
            double sampleStep,
            boolean doDamage,
            double damagePerTick,
            double hitYHalf,
            Color color
    ) {
        this.caster = caster;
        this.warlordsNPC = warlordsNPC;
        this.centerSupplier = centerSupplier;

        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.speedDegPerTick = speedDegPerTick;
        this.durationTicks = Math.max(1, durationTicks);
        this.thickness = Math.max(0.1, thickness);
        this.sampleStep = Math.max(0.15, sampleStep);
        this.doDamage = doDamage;
        this.damagePerTick = Math.max(0.0, damagePerTick);
        this.hitYHalf = Math.max(1.0, hitYHalf);

        this.telegraphParticle = Particle.DUST;
        this.dust = new Particle.DustOptions(color, 1.2f);
        this.floorYOffset = 0.01;
    }

    /* ---------------- Public API ---------------- */

    public void start(Game game) {
        if (running) return;
        running = true;
        ticks = 0;
        angleDeg = 0f;

        task = new GameRunnable(game) {
            @Override
            public void run() {
                Location center = safeCenter();
                World w = center.getWorld();
                if (w == null) { stop(); cancel(); return; }

                ticks++;
                angleDeg = (angleDeg + speedDegPerTick) % 360f;
                double theta = Math.toRadians(angleDeg);

                // Direction unit vector of the wall axis in XZ
                double dx = Math.cos(theta);
                double dz = Math.sin(theta);

                // Draw the full diameter with particles, floor-snapped
                drawDiameterWall(w, center, dx, dz, arenaRadius);

                // Optional damage along the diameter bandwidth
                if (doDamage && damagePerTick > 0) {
                    applyDamageAlongDiameter(center, dx, dz, arenaRadius);
                }

                if (ticks >= durationTicks) {
                    // end
                    w.playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 5f, 1.2f);
                    stop();
                    cancel();
                }
            }

            @Override
            public void cancel() {
                super.cancel();
                // no recursive calls
            }
        };
        task.runTaskTimer(0, 1);
    }

    public void stop() {
        running = false;
        if (task != null) {
            GameRunnable r = task;
            task = null;
            r.cancel();
        }
    }

    public boolean isRunning() {
        return running;
    }

    /* ---------------- Internals ---------------- */

    private final double wallHeight = 5; // total wall height
    private final double yStep = 0.35;     // vertical spacing

    private void drawDiameterWall(World w, Location center, double dx, double dz, double radius) {
        final double step = sampleStep <= 0 ? 0.35 : sampleStep;  // horizontal spacing along the wall
        final int columns = Math.max(1, (int) Math.ceil((2 * radius) / step));
        final int rows    = Math.max(1, (int) Math.ceil(wallHeight / yStep));

        final Particle ptype = Particle.FLAME;
        final int perPoint = 1;

        for (int ci = 0; ci <= columns; ci++) {
            double t = -radius + ci * step;
            double x = center.getX() + dx * t;
            double z = center.getZ() + dz * t;

            // base Y — use ground follow or flat Y; here flat for clarity
            double baseY = center.getY() - 1;

            // if you want ground-follow per column, uncomment:
            // baseY = w.getHighestBlockYAt((int)Math.floor(x), (int)Math.floor(z)) + 0.05;

            for (int ri = 0; ri <= rows; ri++) {
                double y = baseY + ri * yStep;
                //w.spawnParticle(ptype, x, y, z, perPoint, 0, 0, 0, 0.0);
                // For colored dust instead:
                Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(100,180,255), 1.2f);
                w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
            }
        }
    }

    private void applyDamageAlongDiameter(Location center, double dx, double dz, double radius) {
        // Gather enemies in a single AABB around the whole diameter for efficiency
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(center, radius + thickness + 1.0, hitYHalf, radius + thickness + 1.0)
                .aliveEnemiesOf(caster)
        ) {
            Location p = enemy.getLocation();
            if (p.getWorld() != center.getWorld()) continue;

            // Vector math in XZ: project (P - O) onto D = (dx, dz)
            double px = p.getX() - center.getX();
            double pz = p.getZ() - center.getZ();

            double proj = px * dx + pz * dz; // signed along the diameter
            // clamp to segment [-R, +R]
            double t = Math.max(-radius, Math.min(radius, proj));

            // closest point H on the diameter
            double hx = t * dx;
            double hz = t * dz;

            // horizontal distance from player to H
            double distSq = (px - hx) * (px - hx) + (pz - hz) * (pz - hz);
            if (distSq <= thickness * thickness) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Spinning Wall")
                        .value((float) damagePerTick)
                        .source(warlordsNPC)
                );
            }
        }
    }

    private static double groundY(World w, double x, double z) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);
        return w.getHighestBlockYAt(bx, bz);
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().getFirst(), 0, 0, 0) : c.clone();
    }
}
