package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ThunderLineBarrageAbility {

    private final WarlordsNPC source;
    private final Supplier<Location> originSupplier;

    private final int telegraphTicks;
    private final int strikeInterval;
    private final int strikesCount;
    private final double lineLength;
    private final double width;
    private final double damagePerStrike;
    private final double verticalHalf;
    private final double step;

    private final Particle.DustOptions telegraphDust = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 2.8f);
    private final Particle.DustOptions strikeDust = new Particle.DustOptions(Color.fromRGB(180, 255, 255), 4.5f);

    private GameRunnable loop;
    private boolean running = false;

    private List<Location> strikePoints;
    private int lastStrikeIndex = -1;

    public ThunderLineBarrageAbility(
            WarlordsNPC source,
            Supplier<Location> originSupplier,
            int telegraphTicks,
            int strikeInterval,
            int strikesCount,
            double lineLength,
            double width,
            double damagePerStrike,
            double verticalHalf,
            double step
    ) {
        this.source = source;
        this.originSupplier = originSupplier;

        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.strikeInterval = Math.max(1, strikeInterval);
        this.strikesCount = Math.max(1, strikesCount);
        this.lineLength = Math.max(1.0, lineLength);
        this.width = Math.max(0.1, width);
        this.damagePerStrike = Math.max(0.0, damagePerStrike);
        this.verticalHalf = Math.max(1.0, verticalHalf);
        this.step = Math.max(0.1, step);
    }

    public void start(Game game) {
        if (running) return;
        running = true;
        lastStrikeIndex = -1;
        strikePoints = null;

        loop = new GameRunnable(game) {
            int t = 0;
            boolean initialized = false;

            @Override
            public void run() {
                t++;
                Location origin = safeOrigin();
                World w = origin.getWorld();
                if (w == null) { stop(); cancel(); return; }

                if (!initialized) {
                    initialized = true;
                    strikePoints = computeRotatedStrikePoints(origin, strikesCount, lineLength, w);
                }

                // telegraph
                if (t <= telegraphTicks) {

                    float scale = 1.0f + (float) (Math.sin((double) t / 3.0) * 0.18f);
                    drawTelegraph(w, strikePoints);
                    if (t == telegraphTicks) {
                        Utils.playGlobalSound(origin, Sound.BLOCK_BEACON_ACTIVATE, 8, 1.05f);
                    }
                    return;
                }

                int strikeT = t - telegraphTicks; // ticks since strikes began
                int index = strikeT / strikeInterval;

                if (index < 0) return;

                while (lastStrikeIndex < index && lastStrikeIndex + 1 < strikePoints.size()) {
                    lastStrikeIndex++;
                    Location strikeLoc = strikePoints.get(lastStrikeIndex);
                    performStrike(w, strikeLoc);
                }

                if (lastStrikeIndex >= strikePoints.size() - 1) {
                    Utils.playGlobalSound(origin, Sound.BLOCK_BEACON_DEACTIVATE, 8, 1.18f);
                    stop();
                    cancel();
                    return;
                }

                drawTelegraph(w, strikePoints);
            }

            @Override
            public void cancel() {
                super.cancel();
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

    public boolean isRunning() {
        return running;
    }

    private List<Location> computeRotatedStrikePoints(Location center, int count, double length, World w) {
        List<Location> pts = new ArrayList<>(count);

        float yaw = (float) (Math.random() * 360); // rotate on Y

        Vector dir = new Vector(1, 0, 0);
        dir.rotateAroundY(Math.toRadians(yaw));
        dir.normalize();

        double half = length * 0.5;

        for (int i = 0; i < count; i++) {
            double frac = (count == 1) ? 0.0 : (i / (double)(count - 1));
            double dist = frac * length - half;

            Vector offset = dir.clone().multiply(dist);
            pts.add(center.clone().add(offset));
        }

        return pts;
    }

    private void drawTelegraph(World w, List<Location> pts) {
        for (int i = 0; i < pts.size(); i++) {
            Location p = pts.get(i);
            if (i < pts.size() - 1) {
                Location a = pts.get(i);
                Location b = pts.get(i + 1);
                Vector v = b.toVector().subtract(a.toVector());
                double segLen = v.length();
                Vector dir = v.clone().normalize();
                for (double d = 0; d < segLen; d += step) {
                    double x = a.getX() + dir.getX() * d;
                    double y = a.getY() + 0.2; // slightly above ground
                    double z = a.getZ() + dir.getZ() * d;
                    w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, telegraphDust);
                }
            }
        }
    }

    private void performStrike(World w, Location strikeLoc) {
        w.spawnParticle(Particle.ELECTRIC_SPARK, strikeLoc.clone().add(0, 1.0, 0), 12, 0.25, 0.25, 0.25, 0.0);
        w.spawnParticle(Particle.DUST, strikeLoc.clone().add(0, 0.5, 0), 4, 0.12, 0.12, 0.12, 0.0, strikeDust);
        w.spawnParticle(Particle.END_ROD, strikeLoc.clone().add(0, 1.2, 0), 2, 0.03, 0.03, 0.03, 0.0);
        EffectUtils.strikeLightning(strikeLoc, false);

        Utils.playGlobalSound(strikeLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.4f, 0.9f);

        double queryRadius = Math.max(width, 2.0);
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(strikeLoc, queryRadius + 0.2, verticalHalf, queryRadius + 0.2)
                .aliveEnemiesOf(source)
        ) {
            Location p = enemy.getLocation();
            if (!strikeLoc.getWorld().equals(p.getWorld())) continue;
            Location a = strikePoints.getFirst();
            Location b = strikePoints.getLast();

            Vector P = p.add(0, enemy.getEntity().getHeight() * 0.5, 0).toVector();
            Vector A = a.toVector();
            Vector B = b.toVector();
            double dist = distancePointToSegment(P, A, B);

            if (dist <= width) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Acceleration")
                        .value((float) damagePerStrike)
                        .source(source)
                );
                enemy.addSpeedModifier(source, "Thunder Slow", -50, 20);
            }
        }
    }

    private static double distancePointToSegment(Vector p, Vector a, Vector b) {
        // auto generated but it works so
        Vector ab = b.clone().subtract(a);
        double ab2 = ab.lengthSquared();
        if (ab2 < 1e-9) return p.clone().subtract(a).length();
        double t = p.clone().subtract(a).dot(ab) / ab2;
        t = Math.max(0, Math.min(1, t));
        Vector proj = a.clone().add(ab.multiply(t));
        return p.clone().subtract(proj).length();
    }

    private Location safeOrigin() {
        Location o = originSupplier.get();
        return (o == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 64, 0) : o.clone();
    }
}
