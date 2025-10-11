package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class OrbitalStrikeAbility {

    private final WarlordsNPC source;
    /**
     * Supplies the STRIKE TARGET position each tick during telegraph
     * (e.g., () -> player.getLocation()).
     * After lockTicks, the last sampled position is used.
     */
    private final Supplier<Location> targetSupplier;


    private final int lockTicks;          // how long to track target before locking (telegraph duration)
    private final int beamTicks;          // active beam duration after lock
    private final int tickPeriod;         // damage period while beam is active
    private final double beamRadius;      // cylinder radius
    private final double skyOffset;       // how high above impact the beam starts (visual)
    private final double maxTrace;        // max vertical trace distance (in case void/caves)
    private final float tickDamageMin;
    private final float tickDamageMax;
    private final boolean finalBlast;
    private final double finalBlastRadius;
    private final float finalBlastMin;
    private final float finalBlastMax;

    // --- Visuals / SFX ---
    private final Particle telegraphParticle = Particle.DUST;
    private final DustOptions telegraphDust = new DustOptions(Color.fromRGB(255, 105, 180), 1.5f);
    private final DustOptions lockDust = new DustOptions(Color.fromRGB(255, 0, 0), 1.5f);// pink
    private final Particle beamCore = Particle.END_ROD;
    private final Particle beamShell = Particle.ELECTRIC_SPARK;
    private final Particle impactPop = Particle.CHERRY_LEAVES;
    private final Sound telegraphSfx = Sound.BLOCK_AMETHYST_CLUSTER_BREAK;
    private final Sound lockSfx = Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM;
    private final Sound tickSfx = Sound.BLOCK_BEACON_AMBIENT;
    private final Sound blastSfx = Sound.ENTITY_GENERIC_EXPLODE;

    private final Random rng = new Random();

    public OrbitalStrikeAbility(
            @Nonnull WarlordsNPC source,
            @Nonnull Supplier<Location> targetSupplier,
            // timing
            int lockTicks,
            int beamTicks,
            int tickPeriod,
            // geometry
            double beamRadius,
            double skyOffset,
            double maxTrace,
            // damage
            float tickDamageMin,
            float tickDamageMax,
            boolean finalBlast,
            double finalBlastRadius,
            float finalBlastMin,
            float finalBlastMax
    ) {
        this.source = source;
        this.targetSupplier = targetSupplier;

        this.lockTicks = Math.max(5, lockTicks);
        this.beamTicks = Math.max(10, beamTicks);
        this.tickPeriod = Math.max(1, tickPeriod);

        this.beamRadius = Math.max(0.5, beamRadius);
        this.skyOffset = Math.max(4.0, skyOffset);
        this.maxTrace = Math.max(8.0, maxTrace);

        this.tickDamageMin = Math.max(0f, tickDamageMin);
        this.tickDamageMax = Math.max(this.tickDamageMin, tickDamageMax);
        this.finalBlast = finalBlast;
        this.finalBlastRadius = Math.max(0.0, finalBlastRadius);
        this.finalBlastMin = Math.max(0f, finalBlastMin);
        this.finalBlastMax = Math.max(this.finalBlastMin, finalBlastMax);
    }

    /** Fire one orbital strike sequence. */
    public void cast() {
        new GameRunnable(source.getGame()) {
            int t = 0;
            Location lockedImpact = null;

            @Override
            public void run() {
                // TELEGRAPH / LOCK PHASE
                if (t < lockTicks) {
                    Location follow = safeGroundPoint(sampleTarget());
                    if (follow == null) {
                        t++;
                        return;
                    }
                    lockedImpact = follow.clone(); // keep updating until lock ends

                    // draw telegraph ring + subtle sparkle
                    drawRingDust(lockedImpact, beamRadius, 24, telegraphDust);
                    lockedImpact.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, lockedImpact, 8, .35, .1, .35, 0.0);

                    if (t % 6 == 0) {
                        lockedImpact.getWorld().playSound(lockedImpact, telegraphSfx, 2, 0.5f);
                    }

                    if (t == 1) {
                        ChatUtils.sendTitleToGamePlayers(
                                source.getGame(),
                                Component.empty(),
                                Component.text("Orbital strike incoming!", TextColor.color(200, 30, 30)),
                                20, 30, 20
                        );
                    }

                    t++;
                    return;
                }

                // On the very first beam tick: lock and start beam loop
                this.cancel();
                if (lockedImpact == null) return;
                final Location impact = lockedImpact.clone();

                // Compute beam start above impact (sky ray up if wanted; simplest: fixed offset)
                Location beamStart = impact.clone().add(0, skyOffset, 0);

                // Optional: raytrace downward to align to first solid surface precisely
                Location exactImpact = traceDownToSurface(beamStart, maxTrace);
                if (exactImpact != null) {
                    // ensure beamStart is above the surface for visuals
                    impact.set(exactImpact.getX(), exactImpact.getY(), exactImpact.getZ());
                    beamStart = exactImpact.clone().add(0, Math.max(2.0, skyOffset), 0);
                }

                // Fire the beam
                runBeam(beamStart, impact);
            }
        }.runTaskTimer(0, 1);
    }

    // ---- internals ----

    /** Beam loop: visuals + periodic damage + optional final blast. */
    private void runBeam(Location start, Location impact) {
        impact.getWorld().playSound(impact, lockSfx, 10, 1.2f);
        impact.getWorld().playSound(impact, Sound.BLOCK_BEACON_ACTIVATE, 2, 0.8f);

        new GameRunnable(source.getGame()) {
            int t = 0;

            @Override
            public void run() {
                if (t >= beamTicks) {
                    // Final blast
                    if (finalBlast && finalBlastRadius > 0 && (finalBlastMin > 0 || finalBlastMax > 0)) {
                        doBlast(impact);
                    }
                    this.cancel();
                    return;
                }

                // Draw vertical beam (sample along the segment)
                drawBeamSegment(start, impact);

                if (t % 5 == 0) {
                    drawRingDust(impact, finalBlastRadius, 50, lockDust);
                }

                // Periodic damage in the cylinder
                if (t % tickPeriod == 0) {
                    impact.getWorld().playSound(impact, tickSfx, 2, 1.6f);
                    double height = Math.max(4.0, start.getY() - impact.getY() + 1.0);
                    PlayerFilter.entitiesAround(impact, beamRadius, height, beamRadius)
                            .aliveEnemiesOf(source)
                            .forEach(wp -> wp.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Orbital Petal")
                                    .source(source)
                                    .min(tickDamageMin)
                                    .max(tickDamageMax)
                            ));
                }

                t++;
            }
        }.runTaskTimer(0, 1);
    }

    private void doBlast(Location at) {
        Utils.playGlobalSound(at, blastSfx, 3, 0.5f);
        Utils.playGlobalSound(at, Sound.ITEM_MACE_SMASH_GROUND, 5, 0.5f);
        at.getWorld().spawnParticle(impactPop, at, 40, .7, .35, .7, 0.05);
        at.getWorld().spawnParticle(Particle.HEART, at, 16, .4, .2, .4, 0.0);
        Utils.spawnFallingBlocks(at, 3, 13, -0.7, 0.3, Material.CHERRY_LEAVES);
        EffectUtils.playFirework(at, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build()
        );
        drawRingDust(at, finalBlastRadius, 15, lockDust);
        PlayerFilter.entitiesAround(at, finalBlastRadius, 4, finalBlastRadius)
                .aliveEnemiesOf(source)
                .forEach(wp -> wp.addInstance(InstanceBuilder
                        .damage()
                        .cause("Orbital Petal")
                        .source(source)
                        .min(finalBlastMin)
                        .max(finalBlastMax)
                        .flags(InstanceFlags.TRUE_DAMAGE)
                ));
    }

    /** Draws a dense vertical beam by sampling particles along the line start->impact. */
    private void drawBeamSegment(Location start, Location impact) {
        double dy = start.getY() - impact.getY();
        int steps = Math.max(8, (int) Math.ceil(dy * 3)); // 3 samples per block
        Vector step = start.clone().subtract(impact).toVector().multiply(1.0 / steps);

        Location p = impact.clone();
        for (int i = 0; i <= steps; i++) {
            // core + shell
            p.getWorld().spawnParticle(beamCore, p, 3, .05, .02, .02, 0.0);
            p.getWorld().spawnParticle(beamShell, p, 3, .25, .25, .25, 0.0);
            p.add(step);
        }

        // impact flash each tick for emphasis
        impact.getWorld().spawnParticle(Particle.CRIT, impact, 6, .25, .1, .25, 0.0);
    }

    /** Sample target safely; null if world missing. */
    private Location sampleTarget() {
        Location raw = targetSupplier.get();
        if (raw == null || raw.getWorld() == null) return null;
        return raw;
    }

    /** Try to snap target to a usable ground point (or find first solid below). */
    private Location safeGroundPoint(Location loc) {
        if (loc == null || loc.getWorld() == null) return null;
        return groundSnap(loc.clone());
    }

    private static Location traceDownToSurface(Location from, double maxDown) {
        var world = from.getWorld();
        if (world == null) return null;

        var res = world.rayTraceBlocks(from, new Vector(0, -1, 0),
                Math.max(1.0, maxDown), FluidCollisionMode.NEVER, true);
        if (res != null && res.getHitBlock() != null) {
            // place impact on top of the hit block
            Block b = res.getHitBlock();
            return new Location(world, b.getX() + 0.5, b.getY() + 1.0, b.getZ() + 0.5);
        }
        return null;
    }

    private static Location groundSnap(Location loc) {
        Location scan = loc.clone();
        for (int i = 0; i < 24; i++) {
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
}