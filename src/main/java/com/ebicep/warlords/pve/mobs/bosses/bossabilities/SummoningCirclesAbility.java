package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.FrostVeil;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SoulReaver;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SummoningCirclesAbility {

    private final WarlordsEntity source;
    private final Supplier<Location> centerSupplier;

    private final int circleCount;        // how many circles spawn
    private final double arenaRadius;     // max distance from center
    private final int disruptTicks;       // time players have to disrupt
    private final double circleRadius;    // size of the circle
    private final double disruptRadius;   // how close a player must be to count as disrupting
    private final PveOption option;
    private final Random rng = new Random();

    private final List<Circle> circles = new ArrayList<>();
    private GameRunnable loop;
    private boolean running = false;

    public SummoningCirclesAbility(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            int circleCount,
            double arenaRadius,
            int disruptTicks,
            double circleRadius,
            double disruptRadius,
            PveOption option
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;
        this.circleCount = Math.max(1, circleCount);
        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.disruptTicks = Math.max(1, disruptTicks);
        this.circleRadius = Math.max(0.5, circleRadius);
        this.disruptRadius = Math.max(0.5, disruptRadius);
        this.option = option;
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        circles.clear();

        Location center = safeCenter();
        World w = center.getWorld();
        if (w == null) { stop(); return; }

        // Pick random circle locations
        for (int i = 0; i < circleCount; i++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double dist = rng.nextDouble() * arenaRadius;
            double x = center.getX() + Math.cos(angle) * dist;
            double z = center.getZ() + Math.sin(angle) * dist;
            double baseY = center.getY();
            circles.add(new Circle(new Location(w, x, groundYExact(w, x, z, baseY), z)));
        }

        loop = new GameRunnable(game) {
            int t = 0;

            @Override
            public void run() {
                t++;

                boolean anyActive = false;
                for (Circle circle : circles) {
                    if (circle.done) continue;
                    anyActive = true;

                    // Draw the circle outline
                    drawCircle(circle.loc, circleRadius, Particle.DUST,
                            new Particle.DustOptions(Color.fromRGB(180, 0, 180), 1.4f));

                    // Check if disrupted
                    boolean disrupted = PlayerFilter
                            .entitiesAround(circle.loc, disruptRadius, 2, disruptRadius)
                            .aliveEnemiesOf(source)
                            .stream()
                            .findAny()
                            .isPresent();

                    if (disrupted) {
                        circle.done = true;
                        circle.loc.getWorld().playSound(circle.loc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 0.5f);
                        circle.loc.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, circle.loc, 6, 0.6, 0.1, 0.6, 0.05);
                        EffectUtils.playFirework(circle.loc, FireworkEffect.builder()
                                .withColor(Color.BLUE)
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .withTrail()
                                .build());
                        continue;
                    }

                    // Expire if timer ends
                    if (t >= disruptTicks) {
                        circle.done = true;
                        spawnAddAt(circle.loc);
                    }
                }

                if (!anyActive) {
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
            loop.cancel();
            loop = null;
        }
        circles.clear();
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void drawCircle(Location center, double radius, Particle type, Particle.DustOptions dust) {
        World w = center.getWorld();
        if (w == null) return;
        final double twoPi = Math.PI * 2;
        final double step = 0.3 / radius;
        for (double a = 0; a < twoPi; a += step) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            w.spawnParticle(type, x, center.getY() + 0.05, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private void spawnAddAt(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;
        Utils.playGlobalSound(loc, Sound.ENTITY_WITHER_SPAWN, 2, 0.7f);
        w.spawnParticle(Particle.SMOKE, loc, 40, 1, 0.3, 1, 0.05);

        for (int i = 0; i < option.playerCount(); i++) {
            option.spawnNewMob(new FrostVeil(loc.clone().add(0, 1, 0)));
        }
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 64, 0) : c.clone();
    }

    private static double groundYExact(World w, double x, double z, double baseY) {
        // just stick circles slightly above your arena’s flat floor
        return baseY + 0.05;
    }

    /* ---------------- Data ---------------- */
    private static final class Circle {
        final Location loc;
        boolean done = false;

        Circle(Location loc) { this.loc = loc; }
    }
}
