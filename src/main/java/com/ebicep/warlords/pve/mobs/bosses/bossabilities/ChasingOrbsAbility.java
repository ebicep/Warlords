package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.NoTargetAbilities;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChasingOrbsAbility {

    private final WarlordsEntity caster;

    private final int orbCount;
    private final int lifetimeTicks;
    private final double speed;          // blocks per tick
    private final double explosionRadius;
    private final double explosionDamage;
    private final double explosionKnockback;
    private Material material;
    private float materialScale;
    private boolean isHealing;
    private Location startLocation;

    private final List<Orb> orbs = new ArrayList<>();
    private GameRunnable loop;
    private final Random rng = new Random();
    private boolean running = false;

    public ChasingOrbsAbility(
            WarlordsEntity caster,
            int orbCount,
            int lifetimeTicks,
            double speed,
            double explosionRadius,
            double explosionDamage,
            double explosionKnockback,
            Material material,
            float materialScale,
            boolean isHealing,
            Location startLocation
    ) {
        this.caster = caster;
        this.orbCount = orbCount;
        this.lifetimeTicks = lifetimeTicks;
        this.speed = speed;
        this.explosionRadius = explosionRadius;
        this.explosionDamage = explosionDamage;
        this.explosionKnockback = explosionKnockback;
        this.material = material;
        this.materialScale = materialScale;
        this.isHealing = isHealing;
        this.startLocation = startLocation;
    }

    public void start(Game game) {
        if (running) return;
        running = true;

        World w = caster.getEntity().getWorld();

        // Spawn orb displays
        for (int i = 0; i < orbCount; i++) {
            List<WarlordsEntity> targets;
            if (isHealing) {
                targets = PlayerFilter
                        .playingGame(game)
                        .aliveTeammatesOf(caster)
                        .excludingAlliedMobs()
                        .leastAliveFirst()
                        .toList();
            } else {
                targets = PlayerFilter
                        .playingGame(game)
                        .aliveEnemiesOf(caster)
                        .toList();
            }

            if (targets.isEmpty()) continue;

            WarlordsEntity target = targets.get(rng.nextInt(targets.size()));

            Location spawnLoc = startLocation.clone().add(0, 6, 0);
            ItemDisplay display = w.spawn(spawnLoc, ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(material));
                d.setBillboard(Display.Billboard.CENTER);
                d.setViewRange(48f);
                d.setShadowRadius(0f);
                d.setShadowStrength(0f);
                d.setInterpolationDuration(2);
                d.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf(),
                        new Vector3f(materialScale, materialScale, materialScale),
                        new Quaternionf()
                ));
            });

            Orb orb = new Orb(display, target, spawnLoc, 0);
            orbs.add(orb);

            Utils.playGlobalSound(spawnLoc, Sound.ENTITY_ENDER_EYE_LAUNCH, 10, 0.5f);
            Utils.playGlobalSound(spawnLoc, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 10, 0.5f);
        }

        loop = new GameRunnable(game) {
            int tick = 0;

            @Override
            public void run() {
                tick++;

                for (Orb orb : new ArrayList<>(orbs)) {
                    if (!orb.display.isValid()) {
                        orbs.remove(orb);
                        continue;
                    }

                    // Move toward target (with slight lag)
                    Location targetLoc = orb.target.getLocation().clone().add(0, 1, 0);
                    Vector dir = targetLoc.toVector().subtract(orb.current.toVector()).normalize().multiply(speed);

                    orb.current.add(dir);
                    orb.display.teleport(orb.current);

                    orb.display.getWorld().spawnParticle(Particle.END_ROD, orb.current, 1, 0, 0, 0, 0);

                    orb.age++;

                    // Explosion check
                    if (orb.age >= lifetimeTicks) {
                        explode(orb);
                    }
                }

                if (tick >= lifetimeTicks + 20 || orbs.isEmpty()) {
                    stop();
                    cancel();
                }
            }
        };
        loop.runTaskTimer(0, 1);
    }

    private void explode(Orb orb) {
        World w = orb.display.getWorld();
        Location at = orb.current.clone();

        orb.display.remove();
        orbs.remove(orb);

        w.spawnParticle(Particle.EXPLOSION, at, 1, 0, 0, 0, 0);
        Utils.playGlobalSound(at, isHealing ? Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED : Sound.ENTITY_GENERIC_EXPLODE, 2, 0.6f);
        Utils.playGlobalSound(at, Sound.ENTITY_ENDER_EYE_DEATH, 10, 0.5f);

        // Damage + knockback
        if (isHealing) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(at, explosionRadius, 3, explosionRadius)
                    .aliveTeammatesOf(caster)
            ) {
                enemy.addInstance(InstanceBuilder
                        .healing()
                        .cause("Seeking Remedy")
                        .value((float) explosionDamage)
                        .source(caster)
                );
            }
        } else {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(at, explosionRadius, 3, explosionRadius)
                    .aliveEnemiesOf(caster)
            ) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Seeking Vectors")
                        .value((float) explosionDamage)
                        .source(caster)
                );

                if (explosionKnockback > 0) {
                    Utils.addKnockback("Seeking Vectors", at, enemy, explosionKnockback, 0.3);
                }
            }
        }
    }

    public void stop() {
        running = false;
        if (loop != null) {
            loop.cancel();
            loop = null;
        }
        for (Orb orb : orbs) {
            if (orb.display.isValid()) orb.display.remove();
        }
        orbs.clear();
    }

    /* ----------------- Data ----------------- */
    private static class Orb {
        final ItemDisplay display;
        final WarlordsEntity target;
        Location current;
        int age;

        Orb(ItemDisplay display, WarlordsEntity target, Location current, int age) {
            this.display = display;
            this.target = target;
            this.current = current;
            this.age = age;
        }
    }
}
