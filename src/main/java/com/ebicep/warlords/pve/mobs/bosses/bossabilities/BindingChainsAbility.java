package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Supplier;

/**
 * Veilkeeper - Binding Chains
 * Binds N enemy players together. While bound, if the maximum pairwise distance between them
 * exceeds maxLinkDistance, the group takes heavy damage at a fixed tick interval.
 * Draws chain-like particle beams between each pair and a marker above each bound player.
 */
public class BindingChainsAbility {

    private final WarlordsEntity source;

    // ---- Config ----
    private final int targetsToBind;          // how many unique players to bind
    private final int bindDurationTicks;      // how long the binding lasts
    private final double maxLinkDistance;     // max allowed distance between any two bound players
    private final double violationDamage;     // damage dealt per interval when violating
    private final int violationInterval;      // ticks between damage applications while violating
    private final double lineStep;            // particle spacing along chain beams (smaller = denser)
    private final double markerYOffset;       // Y offset for the overhead marker
    private final double ringStep;            // density of the small ring at feet (purely cosmetic)

    // Visuals (DUST)
    private final Particle.DustOptions chainDust
            = new Particle.DustOptions(Color.fromRGB(160, 160, 180), 1.4f);
    private final Particle.DustOptions markerDust
            = new Particle.DustOptions(Color.fromRGB(255, 90, 90), 1.6f);

    // ---- Runtime ----
    private final List<WarlordsEntity> bound = new ArrayList<>();
    private GameRunnable loop;
    private boolean running = false;
    private int internalTick = 0;
    private int lastDamageTick = Integer.MIN_VALUE;

    public BindingChainsAbility(
            WarlordsEntity source,
            int targetsToBind,
            int bindDurationTicks,
            double maxLinkDistance,
            double violationDamage,
            int violationInterval,
            double lineStep,
            double markerYOffset,
            double ringStep
    ) {
        this.source = source;
        this.targetsToBind = Math.max(1, targetsToBind);
        this.bindDurationTicks = Math.max(1, bindDurationTicks);
        this.maxLinkDistance = Math.max(1.0, maxLinkDistance);
        this.violationDamage = Math.max(0.0, violationDamage);
        this.violationInterval = Math.max(1, violationInterval);
        this.lineStep = Math.max(0.15, lineStep);
        this.markerYOffset = markerYOffset;
        this.ringStep = Math.max(0.25, ringStep);
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        bound.clear();
        internalTick = 0;
        lastDamageTick = Integer.MIN_VALUE;

        // Pick up to N unique alive enemies
        List<WarlordsEntity> candidates = PlayerFilter
                .playingGame(game)
                .aliveEnemiesOf(source)
                .toList();

        if (candidates.isEmpty()) { stop(); return; }
        Collections.shuffle(candidates, new Random());

        int pick = Math.min(targetsToBind, candidates.size());
        for (int i = 0; i < pick; i++) {
            bound.add(candidates.get(i));
        }

        // Start SFX
        Utils.playGlobalSound(source.getLocation(), "veilkeeper.binding.start", 1.0f, 1.0f);

        loop = new GameRunnable(game) {
            @Override
            public void run() {
                internalTick++;

                // Cull invalid targets
                bound.removeIf(we -> {
                    if (we == null) return true;
                    we.getLocation();
                    return we.isDead();
                });

                // If fewer than 2 remain, nothing to bind—end.
                if (bound.size() < 2) {
                    cleanupAndStop();
                    return;
                }

                // Draw markers & rings on each bound player
                for (WarlordsEntity we : bound) {
                    Location head = we.getLocation().clone().add(0, markerYOffset, 0);
                    World w = head.getWorld();
                    if (w == null) continue;

                    // Overhead marker
                    w.spawnParticle(Particle.DUST, head, 2, 0.02, 0.02, 0.02, 0.0, markerDust);

                    // Small ground ring at feet
                    drawRing(we.getLocation().clone(), 0.6, markerDust);
                }

                // Draw chain lines between each pair
                drawAllChainBeams(bound);

                // Check violation
                double maxDist = maxPairwiseDistance(bound);
                boolean violating = maxDist > maxLinkDistance;

                if (violating) {
                    // Play a clink/crack hint periodically
                    if ((internalTick % 12) == 0) {
                        Utils.playGlobalSound(source.getLocation(), "veilkeeper.binding.strain", 0.7f, 1.3f);
                    }

                    // Deal damage at fixed intervals while violating
                    if (internalTick - lastDamageTick >= violationInterval) {
                        lastDamageTick = internalTick;
                        applyViolationDamage(bound);
                    }
                }

                // Duration end?
                if (internalTick >= bindDurationTicks) {
                    Utils.playGlobalSound(source.getLocation(), "veilkeeper.binding.end", 0.9f, 1.2f);
                    cleanupAndStop();
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
        bound.clear();
    }

    public boolean isRunning() {
        return running;
    }

    /* ================= Internals ================= */

    private void cleanupAndStop() {
        stop();
    }

    private void applyViolationDamage(List<WarlordsEntity> group) {
        for (WarlordsEntity we : group) {
            we.addInstance(InstanceBuilder
                    .damage()
                    .cause("Binding Chains")
                    .value((float) violationDamage)
                    .source(source)
            );
        }
        // Visual pop on each player
        for (WarlordsEntity we : group) {
            Location at = we.getLocation().clone().add(0, 1, 0);
            World w = at.getWorld();
            if (w != null) {
                w.spawnParticle(Particle.CRIT, at, 8, 0.3, 0.3, 0.3, 0.02);
            }
        }
        Utils.playGlobalSound(source.getLocation(), "veilkeeper.binding.snap", 1.0f, 0.9f);
    }

    private void drawAllChainBeams(List<WarlordsEntity> group) {
        int n = group.size();
        for (int i = 0; i < n; i++) {
            WarlordsEntity a = group.get(i);
            Location la = a.getLocation();

            for (int j = i + 1; j < n; j++) {
                WarlordsEntity b = group.get(j);
                Location lb = b.getLocation();
                if (la.getWorld() != lb.getWorld()) continue;

                drawChainBeam(la, lb, chainDust);
            }
        }
    }

    private void drawChainBeam(Location a, Location b, Particle.DustOptions dust) {
        World w = a.getWorld();
        if (w == null) return;

        // Aim for mid-torso height
        Location aMid = a.clone().add(0, 1.2, 0);
        Location bMid = b.clone().add(0, 1.2, 0);

        Vector dir = bMid.toVector().subtract(aMid.toVector());
        double len = dir.length();
        if (len < 1e-6) return;

        Vector step = dir.normalize().multiply(lineStep);
        int samples = Math.max(1, (int) Math.ceil(len / lineStep));

        Location p = aMid.clone();
        for (int i = 0; i <= samples; i++) {
            w.spawnParticle(Particle.DUST, p, 1, 0, 0, 0, 0.0, dust);
            // subtle metal spark every few samples
            if ((i % 6) == 0) {
                w.spawnParticle(Particle.ELECTRIC_SPARK, p, 1, 0, 0, 0, 0.0);
            }
            p.add(step);
        }
    }

    private void drawRing(Location center, double radius, Particle.DustOptions dust) {
        World w = center.getWorld();
        if (w == null || radius <= 0) return;

        double angStep = Math.max(0.02, ringStep / Math.max(0.1, radius));
        double twoPi = Math.PI * 2.0;
        double y = center.getY() + 0.05; // slight lift to avoid z-fighting
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private static double maxPairwiseDistance(List<WarlordsEntity> list) {
        double max = 0.0;
        int n = list.size();
        for (int i = 0; i < n; i++) {
            Location a = list.get(i).getLocation();
            if (a == null) continue;
            for (int j = i + 1; j < n; j++) {
                Location b = list.get(j).getLocation();
                if (a.getWorld() != b.getWorld()) continue;
                double d = a.distance(b);
                if (d > max) max = d;
            }
        }
        return max;
    }
}
