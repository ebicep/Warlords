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
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LightningChainAbility {

    private final WarlordsNPC source;
    private final Supplier<Location> originSupplier;

    private final double maxDistance;
    private final double damageOnSnap;
    private final int durationTicks;
    private final double particleStep;

    private final Particle.DustOptions chainDust = new Particle.DustOptions(Color.fromRGB(180, 255, 255), 1.8f);

    private GameRunnable loop;
    private boolean running = false;

    private WarlordsEntity playerA;
    private WarlordsEntity playerB;

    public LightningChainAbility(
            WarlordsNPC source,
            Supplier<Location> originSupplier,
            double maxDistance,
            double damageOnSnap,
            int durationTicks,
            double particleStep
    ) {
        this.source = source;
        this.originSupplier = originSupplier;
        this.maxDistance = Math.max(1, maxDistance);
        this.damageOnSnap = Math.max(0, damageOnSnap);
        this.durationTicks = Math.max(1, durationTicks);
        this.particleStep = Math.max(0.05, particleStep);
    }

    public void start(Game game) {
        if (running) return;
        running = true;

        loop = new GameRunnable(game) {
            int t = 0;

            @Override
            public void run() {
                t++;
                Location origin = safeOrigin();
                World w = origin.getWorld();
                if (w == null) { stop(); cancel(); return; }

                if (playerA == null || playerB == null) {
                    List<WarlordsEntity> enemies = new ArrayList<>(PlayerFilter
                            .entitiesAround(origin, 50, 50, 50) // arbitrary large radius
                            .aliveEnemiesOf(source)
                            .stream()
                            .toList());

                    if (enemies.size() < 2) {
                        stop();
                        cancel();
                        return;
                    }

                    Collections.shuffle(enemies);
                    playerA = enemies.get(0);
                    playerB = enemies.get(1);

                    Utils.playGlobalSound(origin, Sound.ENTITY_ENDERMAN_SCREAM, 10, 0.5f);
                }

                if (t % 3 == 0) {
                    drawChainParticles(w, playerA.getLocation(), playerB.getLocation());
                }

                double distance = playerA.getLocation().distance(playerB.getLocation());
                if (distance > maxDistance) {
                    snapChain(playerA.getLocation(), playerB.getLocation());
                    stop();
                    cancel();
                    return;
                }

                if (t >= durationTicks) {
                    stop();
                    cancel();
                }
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

    private void drawChainParticles(World w, Location a, Location b) {
        Vector dir = b.toVector().subtract(a.toVector());
        double length = dir.length();
        dir.normalize();

        for (double d = 0; d < length; d += particleStep) {
            Vector offset = dir.clone().multiply(d);
            Location p = a.clone().add(offset).add(
                    0,
                    1.2 + Math.sin(d * 3) * 0.1, // small sine jitter
                    0
            );
            w.spawnParticle(Particle.DUST, p, 1, 0, 0, 0, 0.0, chainDust);
            w.spawnParticle(Particle.ELECTRIC_SPARK, p, 1, 0, 0, 0, 0.0);
        }
    }

    private void snapChain(Location a, Location b) {
        EffectUtils.strikeLightning(a, false);
        EffectUtils.strikeLightning(b, false);

        Utils.playGlobalSound(a, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.5f, 0.8f);
        Utils.playGlobalSound(b, Sound.ENTITY_GHAST_HURT, 2.5f, 0.5f);

        // snap chain
        for (WarlordsEntity enemy : List.of(playerA, playerB)) {
            if (playerA.isDead() || playerB.isDead()) {
                return;
            }
            enemy.addInstance(
                    InstanceBuilder.damage()
                            .cause("Chain Snap")
                            .value((float) damageOnSnap)
                            .source(source)
            );
            enemy.addSpeedModifier(source, "Chain Slow", -40, 20); // 1 second slow
        }
    }

    private Location safeOrigin() {
        Location o = originSupplier.get();
        return (o == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 64, 0) : o.clone();
    }
}
