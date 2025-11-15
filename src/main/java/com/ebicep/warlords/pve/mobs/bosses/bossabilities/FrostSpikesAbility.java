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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class FrostSpikesAbility {

    public enum Pattern { RING, LINE }        // ring around center, or line from center forward
    public enum EruptMode { SEQUENTIAL, ALL } // sequential wave or all at once

    private final WarlordsEntity caster;             // for aliveEnemiesOf()
    private final WarlordsNPC warlordsNPC;           // damage source
    private final Supplier<Location> centerSupplier; // static center or follows boss

    // Config
    private final Pattern pattern;
    private final EruptMode eruptMode;
    private final double arenaRadius;        // ring radius or line half-length
    private final int spikeCount;            // number of spike sites
    private final int telegraphTicks;        // delay before eruption (per spike for sequential)
    private final int betweenTicks;          // delay between spikes in SEQUENTIAL mode
    private final double impactRadius;       // AoE radius per spike on eruption
    private final double damage;             // damage on eruption
    private final double knockUp;            // upward velocity (0 disables)
    private final double wallHeight;         // visual spike height (particles)
    private final double yStep;              // vertical spacing for spike column particles
    private final boolean followCenter;      // if true, spikes move with center during telegraph
    private final boolean randomizeStart;    // randomize ring start angle / line direction
    private final double ringStep;           // telegraph ring particle density

    // Visual colors
    private final Particle.DustOptions telegraphDust = new Particle.DustOptions(Color.fromRGB(170, 220, 255), 1.1f);
    private final Particle.DustOptions eruptDust     = new Particle.DustOptions(Color.fromRGB(110, 200, 255), 1.4f);

    // Runtime
    private final List<Spike> spikes = new ArrayList<>();
    private GameRunnable task;
    private boolean running = false;
    private final Random rng = new Random();

    public FrostSpikesAbility(
            WarlordsEntity caster,
            WarlordsNPC warlordsNPC,
            Supplier<Location> centerSupplier,
            Pattern pattern,
            EruptMode eruptMode,
            double arenaRadius,
            int spikeCount,
            int telegraphTicks,
            int betweenTicks,
            double impactRadius,
            double damage,
            double knockUp,
            double wallHeight,
            double yStep,
            boolean followCenter,
            boolean randomizeStart,
            double ringStep
    ) {
        this.caster = caster;
        this.warlordsNPC = warlordsNPC;
        this.centerSupplier = centerSupplier;
        this.pattern = pattern;
        this.eruptMode = eruptMode;
        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.spikeCount = Math.max(1, spikeCount);
        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.betweenTicks = Math.max(0, betweenTicks);
        this.impactRadius = Math.max(0.5, impactRadius);
        this.damage = Math.max(0.0, damage);
        this.knockUp = Math.max(0.0, knockUp);
        this.wallHeight = Math.max(1.0, wallHeight);
        this.yStep = Math.max(0.25, yStep);
        this.followCenter = followCenter;
        this.randomizeStart = randomizeStart;
        this.ringStep = Math.max(0.25, ringStep);
    }

    /* ---------------- Public API ---------------- */

    public void start(Game game) {
        if (running) return;
        running = true;

        // Place spike sites
        final Location base = safeCenter();
        placeSpikes(base);

        // Main loop
        task = new GameRunnable(game) {
            int t = 0;
            @Override public void run() {
                if (!running) { cancel(); return; }
                t++;

                Location currentCenter = followCenter ? safeCenter() : base;
                World w = currentCenter.getWorld();
                if (w == null) { stop(); cancel(); return; }

                // Update positions if following
                if (followCenter) {
                    updateSpikePositions(currentCenter);
                }

                boolean anyActive = false;

                for (int i = 0; i < spikes.size(); i++) {
                    Spike s = spikes.get(i);

                    // Telegraph: draw a small ring / marker
                    if (!s.erupted) {
                        drawTelegraphRing(w, s.worldPos, impactRadius, ringStep, t, telegraphDust);
                    }

                    // Determine eruption time per spike
                    int eruptAt = (eruptMode == EruptMode.ALL)
                            ? telegraphTicks
                            : telegraphTicks + (i * betweenTicks);

                    if (!s.erupted && t >= eruptAt) {
                        s.erupted = true;
                        erupt(w, s.worldPos);
                    }

                    // Spikes linger visually just this tick; no per-spike linger timer here.
                    if (!s.erupted || t < eruptAt + 1) {
                        anyActive = true;
                    }
                }

                // End when all erupted (and the frame immediately after to let effects draw)
                if (!anyActive) {
                    stop();
                    cancel();
                }
            }

            @Override public void cancel() {
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
        spikes.clear();
    }

    /* ---------------- Internals ---------------- */

    private void placeSpikes(Location center) {
        spikes.clear();
        World w = center.getWorld();
        if (w == null) return;

        if (pattern == Pattern.RING) {
            double startAng = randomizeStart ? rng.nextDouble() * Math.PI * 2 : 0.0;
            for (int i = 0; i < spikeCount; i++) {
                double a = startAng + i * (Math.PI * 2 / spikeCount);
                double x = center.getX() + Math.cos(a) * arenaRadius;
                double z = center.getZ() + Math.sin(a) * arenaRadius;
                double y = groundY(w, x, z) + 0.01;

                Spike s = new Spike();
                s.offsetR = arenaRadius;
                s.offsetA = a;
                s.worldPos = new Location(w, x, y, z);
                spikes.add(s);
            }
        } else { // LINE: along a diameter; angle chosen (random or 0)
            double a = randomizeStart ? rng.nextDouble() * Math.PI * 2 : 0.0;
            double dx = Math.cos(a), dz = Math.sin(a);
            // sample symmetric positions along the line segment
            double step = (2 * arenaRadius) / Math.max(1, spikeCount - 1);
            for (int i = 0; i < spikeCount; i++) {
                double t = -arenaRadius + i * step;
                double x = center.getX() + dx * t;
                double z = center.getZ() + dz * t;
                double y = groundY(w, x, z) + 0.01;

                Spike s = new Spike();
                s.lineDX = dx; s.lineDZ = dz; s.lineT = t;
                s.worldPos = new Location(w, x, y, z);
                spikes.add(s);
            }

            System.out.println("Spikes: " + spikes.size());
        }

        // initial telegraph sound
        w.playSound(center, Sound.BLOCK_GLASS_STEP, 2, 1.6f);
    }

    private void updateSpikePositions(Location center) {
        World w = center.getWorld();
        if (w == null) return;

        if (pattern == Pattern.RING) {
            for (Spike s : spikes) {
                double x = center.getX() + Math.cos(s.offsetA) * s.offsetR;
                double z = center.getZ() + Math.sin(s.offsetA) * s.offsetR;
                double y = center.getY() + 0.01;
                s.worldPos.set(x, y, z);
                s.worldPos.setWorld(w);
            }
        } else { // LINE
            for (Spike s : spikes) {
                double x = center.getX() + s.lineDX * s.lineT;
                double z = center.getZ() + s.lineDZ * s.lineT;
                double y = center.getY() + 0.01;
                s.worldPos.set(x, y, z);
                s.worldPos.setWorld(w);
            }
        }
    }

    private void erupt(World w, Location at) {
        // VFX + SFX
        w.playSound(at, Sound.BLOCK_GLASS_BREAK, 2f, 0.8f);
        w.playSound(at, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 2f, 1.5f);

        // Vertical “spike” column (icey particles)
        int rows = Math.max(1, (int) Math.ceil(wallHeight / yStep));
        for (int ri = 0; ri <= rows; ri++) {
            double y = at.getY() + ri * yStep;
            // snow + dust mix for a sharp icy look
            w.spawnParticle(Particle.SNOWFLAKE, at.getX(), y, at.getZ(), 2, 0, 0, 0, 0.0);
            w.spawnParticle(Particle.FLAME, at.getX(), y, at.getZ(), 1, 0, 0, 0, 0);
            //w.spawnParticle(Particle.DUST, at.getX(), y, at.getZ(), 1, 0, 0, 0, 0.0, eruptDust);
        }

        // Damage + knock-up
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(at, impactRadius, wallHeight / 2.0 + 1.0, impactRadius)
                .aliveEnemiesOf(caster)
        ) {
            enemy.addInstance(InstanceBuilder
                    .damage()
                    .cause("Frost Spikes")
                    .value((float) damage)
                    .source(warlordsNPC)
            );
            if (knockUp > 0) {
                Utils.addKnockback("Ice Spikes", at, enemy, 1, knockUp);
            }
        }
    }

    private void drawTelegraphRing(World w, Location c, double radius, double arcStep, int tick, Particle.DustOptions dust) {
        // soft pulsing ring
        double twoPi = Math.PI * 2;
        double angStep = Math.max(0.02, arcStep / Math.max(0.1, radius));
        float size = 1.0f + 0.15f * (float) Math.sin(tick * 0.35);
        Particle.DustOptions d = new Particle.DustOptions(dust.getColor(), size);

        for (double a = 0; a < twoPi; a += angStep) {
            double x = c.getX() + Math.cos(a) * radius;
            double z = c.getZ() + Math.sin(a) * radius;
            double y = c.getY(); // already ground-snapped in s.worldPos
            w.spawnParticle(Particle.FLAME, x, y, z, 1, 0, 0, 0, 0);
            //w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, d);
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

    /* ------------ Data ------------ */
    private static final class Spike {
        // Ring-mode parameters
        double offsetR, offsetA;
        // Line-mode parameters
        double lineDX, lineDZ, lineT;
        // Shared
        Location worldPos;
        boolean erupted = false;
    }
}
