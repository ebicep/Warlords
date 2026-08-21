package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class HeavenlySpearAbility {

    private final WarlordsNPC source;
    private final Supplier<Location> centerSupplier;

    private final int spearCount;          // how many spears fall
    private final double arenaRadius;      // max distance from center
    private final int telegraphTicks;      // warning delay before impact
    private final double impactRadius;     // AoE damage radius
    private final double impactDamage;     // damage on impact
    private final int persistTicks;        // how long spears stay
    private final double spearHeight;      // visual spear height
    private final double spearThickness; // visual spear thickness
    private final Material spearMaterial;
    private final Random rng = new Random();
    private Particle telegraphSfx;
    private Particle persistentSfx;
    private Sound impactSoundSfx;
    private Sound itemRemoveSfx;

    private final List<BlockDisplay> spears = new ArrayList<>();
    private GameRunnable loop;
    private boolean running = false;

    public HeavenlySpearAbility(
            WarlordsNPC source,
            Supplier<Location> centerSupplier,
            int spearCount,
            double arenaRadius,
            int telegraphTicks,
            double impactRadius,
            double impactDamage,
            int persistTicks,
            double spearHeight,
            double spearThickness,
            Material spearMaterial, Particle telegraphSfx, Particle persistentSfx, Sound impactSoundSfx, Sound itemRemoveSfx
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;
        this.spearCount = Math.max(1, spearCount);
        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.impactRadius = Math.max(0.5, impactRadius);
        this.impactDamage = Math.max(0.0, impactDamage);
        this.persistTicks = Math.max(1, persistTicks);
        this.spearHeight = spearHeight;
        this.spearThickness = spearThickness;
        this.spearMaterial = spearMaterial;
        this.telegraphSfx = telegraphSfx;
        this.persistentSfx = persistentSfx;
        this.impactSoundSfx = impactSoundSfx;
        this.itemRemoveSfx = itemRemoveSfx;
    }

    public void start(Game game) {
        if (running) return;
        running = true;
        spears.clear();

        Location center = safeCenter();
        World w = center.getWorld();
        if (w == null) { stop(); return; }

        // Pick random landing points in arena circle
        List<Location> landings = new ArrayList<>();
        for (int i = 0; i < spearCount; i++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double dist = rng.nextDouble() * arenaRadius;
            double x = center.getX() + Math.cos(angle) * dist;
            double z = center.getZ() + Math.sin(angle) * dist;
            double y = center.getY();
            landings.add(new Location(w, x, y, z));
        }

        loop = new GameRunnable(game) {
            int t = 0;

            @Override
            public void run() {
                t++;

                // Telegraph phase
                if (t <= telegraphTicks) {
                    for (Location loc : landings) {
                        w.spawnParticle(Particle.DUST, loc.clone().add(0, 0.1, 0), 3, 0.4, 0, 0.4, 0,
                                new Particle.DustOptions(Color.fromRGB(200, 200, 255), 1.5f));
                        if (telegraphSfx != null) {
                            new CircleEffect(
                                    game,
                                    source.getTeam(),
                                    loc.clone().add(0, 0.1, 0),
                                    impactRadius,
                                    new CircumferenceEffect(telegraphSfx, telegraphSfx).particlesPerCircumference(0.25)
                            ).playEffects();
                        }
                        if (t == 1) {
                            Utils.playGlobalSound(centerSupplier.get(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 0.8f);
                        }
                    }
                    return;
                }

                // Impact
                if (t == telegraphTicks + 1) {
                    for (Location loc : landings) {
                        impactAt(loc);
                    }
                    Utils.playGlobalSound(centerSupplier.get(), impactSoundSfx, 500, 0.5f);
                }

                // Spears persist
                if (t > telegraphTicks + 1 && t <= telegraphTicks + persistTicks) {
                    for (BlockDisplay spear : spears) {
                        if (!spear.isValid()) continue;
                        spear.getWorld().spawnParticle(persistentSfx, spear.getLocation(), 2, 0.1, 1, 0.1, 0);
                    }
                }

                // Cleanup
                if (t > telegraphTicks + persistTicks) {
                    for (BlockDisplay spear : spears) {
                        if (spear.isValid()) {
                            spear.remove();
                            spear.getWorld().spawnParticle(Particle.BLOCK_CRUMBLE,
                                    spear.getLocation(), 10, 0.5, 0.5, 0.5, 0,
                                    Material.ICE.createBlockData());
                            Utils.playGlobalSound(spear.getLocation(), itemRemoveSfx, 10, 0.9f);
                        }
                    }
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
        for (BlockDisplay spear : spears) {
            if (spear.isValid()) spear.remove();
        }
        spears.clear();
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void impactAt(Location loc) {
        World w = loc.getWorld();
        if (w == null) return;

        // Explosion effect
        w.spawnParticle(Particle.EXPLOSION, loc, 1, 0, 0, 0, 0);
        Utils.playGlobalSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2, 0.5f);
        // Damage nearby
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(loc, impactRadius, 3, impactRadius)
                .aliveEnemiesOf(source)
        ) {
            enemy.addInstance(InstanceBuilder
                    .damage()
                    .cause("Heavenly Spear")
                    .value((float) impactDamage)
                    .source(source)
            );
        }

        // Spawn spear display
        BlockDisplay spear = w.spawn(loc, BlockDisplay.class, d -> {
            d.setBlock(spearMaterial.createBlockData());
            d.setBillboard(Display.Billboard.FIXED);
            d.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),
                    new Vector3f((float) spearThickness, (float) spearHeight, (float) spearThickness),
                    new Quaternionf()
            ));
        });
        spears.add(spear);
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 64, 0) : c.clone();
    }
}
