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
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Lilium, Queen of Hearts — Rose Garden (with ItemDisplay flowers)
 *
 * Spawns elegant "rose nodes" arranged around a center.
 * Each node:
 *  • Telegraphs briefly (ring of pink petals + visible flower ItemDisplay).
 *  • Becomes active: emits periodic thorn aura (AoE damage + optional slow).
 *  • On expiry (optionally) "blooms" with a burst AoE.
 *
 * All tuning via constructor (CrystalConduitsAbility style).
 */
public class RoseGardenAbility {

    // ---- Required context ----
    private final WarlordsNPC source;
    private final Supplier<Location> centerSupplier;

    // ---- Tuning knobs (aura / timing) ----
    private final int roseCount;
    private final double ringRadius;
    private final double ringJitter;
    private final double nodeRadius;
    private final int telegraphTicks;
    private final int lifetimeTicks;
    private final int tickPeriod;
    private final float tickDamageMin;
    private final float tickDamageMax;
    private final boolean applySlow;
    private final int slowTicks;
    private final int slowAmplifier;

    // Optional bloom burst on expiry
    private final boolean bloomOnExpire;
    private final double bloomRadius;
    private final float bloomDamageMin;
    private final float bloomDamageMax;

    // ---- Visual ItemDisplay flower knobs ----
    private final boolean useFlowerDisplays;
    private final Material flowerMaterial;
    private final double flowerScale;       // uniform scale (1.0 = normal)
    private final boolean spinFlowers;      // rotate around Y
    private final int spinPeriodTicks;      // apply rotation every N ticks
    private final float spinStepDegrees;    // degrees per step

    // ---- Visuals / SFX ----
    private final Particle telegraphParticle = Particle.DUST;
    private final DustOptions telegraphDust = new DustOptions(Color.fromRGB(255, 105, 180), 1.1f); // hot pink
    private final Particle auraParticle = Particle.SPORE_BLOSSOM_AIR;
    private final Particle thornParticle = Particle.DAMAGE_INDICATOR;
    private final Particle bloomParticleA = Particle.HEART;
    private final Particle bloomParticleB = Particle.BLOCK_CRUMBLE;
    private final BlockData bloomBlock = Material.PINK_CONCRETE.createBlockData();

    private final Sound plantSfx = Sound.BLOCK_AZALEA_PLACE;
    private final Sound activateSfx = Sound.BLOCK_AMETHYST_BLOCK_CHIME;
    private final Sound tickSfx = Sound.BLOCK_SWEET_BERRY_BUSH_PICK_BERRIES;
    private final Sound bloomSfx = Sound.ENTITY_GENERIC_EXPLODE;

    private final Random rng = new Random();

    public RoseGardenAbility(
            @Nonnull WarlordsNPC source,
            @Nonnull Supplier<Location> centerSupplier,
            // aura/timing
            int roseCount,
            double ringRadius,
            double ringJitter,
            double nodeRadius,
            int telegraphTicks,
            int lifetimeTicks,
            int tickPeriod,
            float tickDamageMin,
            float tickDamageMax,
            boolean applySlow,
            int slowTicks,
            int slowAmplifier,
            boolean bloomOnExpire,
            double bloomRadius,
            float bloomDamageMin,
            float bloomDamageMax,
            // displays
            boolean useFlowerDisplays,
            @Nonnull Material flowerMaterial,
            double flowerScale,
            boolean spinFlowers,
            int spinPeriodTicks,
            float spinStepDegrees
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        // aura/timing
        this.roseCount = Math.max(1, roseCount);
        this.ringRadius = Math.max(1.0, ringRadius);
        this.ringJitter = Math.max(0.0, ringJitter);
        this.nodeRadius = Math.max(1.0, nodeRadius);
        this.telegraphTicks = Math.max(5, telegraphTicks);
        this.lifetimeTicks = Math.max(20, lifetimeTicks);
        this.tickPeriod = Math.max(5, tickPeriod);
        this.tickDamageMin = Math.max(0f, tickDamageMin);
        this.tickDamageMax = Math.max(tickDamageMin, tickDamageMax);
        this.applySlow = applySlow;
        this.slowTicks = Math.max(0, slowTicks);
        this.slowAmplifier = Math.max(0, slowAmplifier);

        this.bloomOnExpire = bloomOnExpire;
        this.bloomRadius = Math.max(0.0, bloomRadius);
        this.bloomDamageMin = Math.max(0f, bloomDamageMin);
        this.bloomDamageMax = Math.max(bloomDamageMin, bloomDamageMax);

        // displays
        this.useFlowerDisplays = useFlowerDisplays;
        this.flowerMaterial = flowerMaterial;
        this.flowerScale = Math.max(0.1, flowerScale);
        this.spinFlowers = spinFlowers;
        this.spinPeriodTicks = Math.max(1, spinPeriodTicks);
        this.spinStepDegrees = spinStepDegrees;
    }

    /**
     * Plant the rose garden once.
     */
    public void cast() {
        Location center = groundSnap(centerSupplier.get().clone());
        if (center.getWorld() == null) return;

        // Evenly spaced positions on a circle (with slight jitter)
        List<Location> nodeSpawns = new ArrayList<>(roseCount);
        double step = (Math.PI * 2) / roseCount;
        for (int i = 0; i < roseCount; i++) {
            double angle = i * step;
            double radius = ringRadius + (ringJitter > 0 ? (rng.nextDouble() * 2 - 1) * ringJitter : 0.0);
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            Location loc = new Location(center.getWorld(), x, center.getY(), z);
            nodeSpawns.add(groundSnap(loc));
        }

        // Telegraph + activate each node with a per-node runnable
        for (Location nodeLoc : nodeSpawns) {
            plantNode(nodeLoc);
        }
    }

    // --- internals ---

    private void plantNode(Location nodeLoc) {
        // Plant SFX
        nodeLoc.getWorld().playSound(nodeLoc, plantSfx, 0.9f, 1.2f);

        new GameRunnable(source.getGame()) {
            int tick = 0;
            boolean active = false; // becomes true after telegraph phase
            int lifetime = lifetimeTicks;
            ItemDisplay display = null;

            @Override
            public void run() {
                // Spawn display at the beginning of telegraph
                if (tick == 0 && useFlowerDisplays) {
                    display = spawnFlowerDisplay(nodeLoc);
                }

                if (!active) {
                    // Telegraph ring & light petals
                    drawRingDust(nodeLoc, nodeRadius, 20, telegraphDust);
                    nodeLoc.getWorld().spawnParticle(auraParticle, nodeLoc, 6, 0.35, 0.15, 0.35, 0.0);

                    spinDisplayIfNeeded(display, tick);

                    if (tick >= telegraphTicks) {
                        active = true;
                        nodeLoc.getWorld().playSound(nodeLoc, activateSfx, 0.9f, 1.1f);
                        tick = 0; // reuse as period counter for aura
                    } else {
                        tick++;
                        return;
                    }
                }

                // Active phase
                drawRingDust(nodeLoc, nodeRadius, 24, telegraphDust);
                nodeLoc.getWorld().spawnParticle(auraParticle, nodeLoc, 8, 0.45, 0.2, 0.45, 0.0);

                spinDisplayIfNeeded(display, tick);

                // Periodic thorn tick
                if (tick % tickPeriod == 0) {
                    nodeLoc.getWorld().playSound(nodeLoc, tickSfx, 0.6f, 1.4f);

                    PlayerFilter.entitiesAround(nodeLoc, nodeRadius, 3, nodeRadius)
                            .aliveEnemiesOf(source)
                            .forEach(wp -> {
                                wp.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Garden of Despair")
                                        .source(source)
                                        .min(tickDamageMin)
                                        .max(tickDamageMax)
                                );

                                if (applySlow) {
                                    wp.addSpeedModifier(source, "Rose Thorns", -25, slowTicks);
                                }

                                nodeLoc.getWorld().spawnParticle(thornParticle, wp.getLocation(), 2, 0.1, 0.1, 0.1, 0.0);
                            });
                }

                // Lifetime countdown
                lifetime--;
                tick++;

                if (lifetime <= 0) {
                    if (bloomOnExpire && bloomRadius > 0.0 && (bloomDamageMin > 0 || bloomDamageMax > 0)) {
                        bloomBurst(nodeLoc);
                    }
                    if (display != null && !display.isDead()) {
                        display.remove();
                    }
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void bloomBurst(Location at) {
        at.getWorld().playSound(at, bloomSfx, 0.8f, 1.0f);
        at.getWorld().spawnParticle(bloomParticleA, at, 24, 0.5, 0.2, 0.5, 0.0);
        at.getWorld().spawnParticle(bloomParticleB, at, 40, 0.7, 0.35, 0.7, 0.05, bloomBlock);

        PlayerFilter.entitiesAround(at, bloomRadius, 3, bloomRadius)
                .aliveEnemiesOf(source)
                .forEach(wp -> wp.addInstance(InstanceBuilder
                        .damage()
                        .cause("Bloom Burst")
                        .source(source)
                        .min(bloomDamageMin)
                        .max(bloomDamageMax)
                ));
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

    // ==== ItemDisplay helpers ====

    private ItemDisplay spawnFlowerDisplay(@NotNull Location loc) {
        try {
            // Slightly raise so it sits above ground
            Location at = loc.clone().add(0, 0.05, 0);

            ItemDisplay display = at.getWorld().spawn(at.add(0, 3, 0), ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(flowerMaterial));
                d.setBillboard(Billboard.FIXED);

                // Make sure it's upright and scaled
                float s = (float) flowerScale;
                Transformation t = new Transformation(
                        new Vector3f(0f, 0f, 0f),
                        new Quaternionf(),                  // left rotation
                        new Vector3f(s, s, s),              // scale
                        new Quaternionf()                   // right rotation
                );
                d.setTransformation(t);
                d.setPersistent(true);
                d.setInvulnerable(true);
                d.setGlowing(false);
            });

            return display;
        } catch (Throwable ignored) {
            // If spawning fails for any reason, just skip the display.
            return null;
        }
    }

    private void spinDisplayIfNeeded(ItemDisplay display, int tickCount) {
        if (!spinFlowers || display == null || display.isDead()) return;
        if (tickCount % spinPeriodTicks != 0) return;

        try {
            Transformation t = display.getTransformation();
            Quaternionf right = new Quaternionf(t.getRightRotation());
            // rotate around Y axis
            float radians = (float) Math.toRadians(spinStepDegrees);
            right.rotateY(radians);

            Transformation updated = new Transformation(
                    new Vector3f(t.getTranslation()),
                    new Quaternionf(t.getLeftRotation()),
                    new Vector3f(t.getScale()),
                    right
            );
            display.setTransformation(updated);
        } catch (Throwable ignored) {
            // If anything goes wrong with transformation, fail silently to avoid ticking errors.
        }
    }
}