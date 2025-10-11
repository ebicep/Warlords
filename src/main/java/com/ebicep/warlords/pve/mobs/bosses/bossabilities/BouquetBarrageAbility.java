package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BouquetBarrageAbility {

    private final WarlordsNPC source;
    private final int bouquetsPerCast;     // number of bouquets per cast
    private final double maxRange;         // max range to pick initial targets from source
    private final int telegraphTicks;      // delay before bloom (ticks)
    private final double bloomRadius;      // AoE radius at impact
    private final float damageMin;         // damage min
    private final float damageMax;         // damage max
    private final boolean applySlow;
    private final int slowTicks;
    private final int slowAmplifier;

    private final Particle trailParticle = Particle.CHERRY_LEAVES;
    private final DustOptions telegraphDust = new DustOptions(Color.fromRGB(255, 105, 180), 2f); // hot pink
    private final Particle bloomParticleA = Particle.HEART;
    private final Particle bloomParticleB = Particle.BLOCK_CRUMBLE;
    private final BlockData bloomBlock = Material.PINK_CONCRETE.createBlockData();
    private final Sound tossSfx = Sound.ENTITY_ENDER_PEARL_THROW;
    private final Sound plantSfx = Sound.BLOCK_AZALEA_PLACE;
    private final Sound bloomSfx = Sound.BLOCK_BEACON_DEACTIVATE;

    private final double tossArcMaxY = 1.25; // simple vertical arc height for the particle trail

    public BouquetBarrageAbility(
            @Nonnull WarlordsNPC source,
            int bouquetsPerCast,
            double maxRange,
            int telegraphTicks,
            double bloomRadius,
            float damageMin,
            float damageMax,
            boolean applySlow,
            int slowTicks,
            int slowAmplifier
    ) {
        this.source = source;
        this.bouquetsPerCast = Math.max(1, bouquetsPerCast);
        this.maxRange = Math.max(1.0, maxRange);
        this.telegraphTicks = Math.max(10, telegraphTicks);
        this.bloomRadius = Math.max(1.0, bloomRadius);
        this.damageMin = damageMin;
        this.damageMax = Math.max(damageMin, damageMax);
        this.applySlow = applySlow;
        this.slowTicks = Math.max(0, slowTicks);
        this.slowAmplifier = Math.max(0, slowAmplifier);
    }

    public void cast() {
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(source, maxRange, maxRange, maxRange)
                .aliveEnemiesOf(source)
                .closestFirst(source)
                .limit(bouquetsPerCast)
                .toList();

        if (targets.isEmpty()) {
            return;
        }

        List<Impact> impacts = new ArrayList<>(targets.size());
        for (WarlordsEntity t : targets) {
            Location impact = groundSnap(t.getLocation().clone());
            impacts.add(new Impact(impact));
        }

        source.getLocation().getWorld().playSound(source.getLocation(), tossSfx, 2, 0.5f);

        new GameRunnable(source.getGame()) {
            int tick = 0;

            @Override
            public void run() {
                if (tick < telegraphTicks) {
                    // particle trail + telegraph ring
                    Location src = source.getLocation();
                    for (Impact impact : impacts) {
                        // trail arc from source -> impact
                        double progress = (tick + 1) / (double) telegraphTicks;
                        Location mid = src.clone().add(
                                (impact.loc.getX() - src.getX()) * progress,
                                (impact.loc.getY() - src.getY()) + Math.sin(progress * Math.PI) * tossArcMaxY,
                                (impact.loc.getZ() - src.getZ()) * progress
                        );
                        src.getWorld().spawnParticle(trailParticle, mid, 3, 0.05, 0.05, 0.05, 0.0);

                        // telegraph ring at impact (pink dust)
                        drawRingDust(impact.loc, bloomRadius, 20, telegraphDust);
                    }

                    // subtle “planting” cue shortly before bloom
                    if (tick == telegraphTicks - 10) {
                        for (Impact impact : impacts) {
                            impact.loc.getWorld().playSound(impact.loc, plantSfx, 2, 1.2f);
                        }
                    }

                    tick++;
                    return;
                }

                for (Impact impact : impacts) {
                    // SFX + particles
                    impact.loc.getWorld().playSound(impact.loc, bloomSfx, 2, 0.5f);
                    impact.loc.getWorld().spawnParticle(bloomParticleA, impact.loc, 20, 0.4, 0.15, 0.4, 0.0);
                    impact.loc.getWorld().spawnParticle(bloomParticleB, impact.loc, 30, 0.6, 0.3, 0.6, 0.05, bloomBlock);

                    PlayerFilter.entitiesAround(impact.loc, bloomRadius, 3, bloomRadius)
                            .aliveEnemiesOf(source)
                            .forEach(wp -> {
                                wp.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Bouquet Barrage")
                                        .source(source)
                                        .min(damageMin)
                                        .max(damageMax)
                                );

                                if (applySlow) {
                                    wp.addSpeedModifier(source, "Bouquet Thorns", -30, slowTicks);
                                }
                            });
                }

                this.cancel();
            }
        }.runTaskTimer(0, 1);
    }

    private static Location groundSnap(Location loc) {
        Location scan = loc.clone();
        for (int i = 0; i < 16; i++) {
            if (scan.getBlock().getType().isSolid()) {
                return scan.add(0, 1, 0);
            }
            scan.subtract(0, 1, 0);
        }
        return loc;
    }

    private static void drawRingDust(Location center, double radius, int points, DustOptions dust) {
        final double step = (Math.PI * 2) / points;
        for (int i = 0; i < points; i++) {
            double angle = i * step;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            Location p = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnParticle(Particle.DUST, p, 1, dust);
        }
    }

    // holder for impact locations
    private static class Impact {
        final Location loc;
        Impact(Location loc) {
            this.loc = loc;
        }
    }
}