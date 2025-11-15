package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Color;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class FallingItemSmashAbility {

    // ---- Context ----
    private final WarlordsNPC source;
    private final Supplier<Location> centerSupplier;

    // ---- Targeting / pattern ----
    private final int count;               // number of falling items per cast
    private final double areaHalfSizeX;    // randomize targets in box around center (±X)
    private final double areaHalfSizeZ;    // randomize targets in box around center (±Z)

    // ---- Fall & timing ----
    private final double spawnHeight;      // Y offset above ground where the item spawns
    private final int fallTicks;           // ticks to complete the fall (visual timing)
    private final int lingerTicks;         // how long the display stays after impact

    // ---- Impact ----
    private final double impactRadius;     // AoE radius
    private final float damageMin;
    private final float damageMax;
    private final boolean applySlow;
    private final int slowTicks;
    private final int slowPercent;         // -X% speed
    private final boolean knockback;
    private final double knockbackStrength;

    // ---- Display visuals ----
    private final Material displayMaterial; // e.g., Material.NETHERITE_SWORD
    private final double scale;             // uniform scale of the display
    private final boolean glow;             // set glowing flag on display

    // ---- VFX / SFX ----
    private final Particle telegraphParticle = Particle.DUST;
    private final DustOptions telegraphDust = new DustOptions(Color.fromRGB(255, 105, 180), 1.05f);
    private final Particle fallTrail = Particle.SPORE_BLOSSOM_AIR;
    private final Particle impactPop = Particle.BLOCK_CRUMBLE;
    private final BlockData popBlock = Material.PINK_CONCRETE.createBlockData();
    private final Particle heart = Particle.HEART;
    private final Sound spawnSfx = Sound.BLOCK_AMETHYST_BLOCK_RESONATE;
    private final Sound fallWhooshSfx = Sound.ENTITY_PHANTOM_FLAP;
    private final Sound impactSfx = Sound.ENTITY_GENERIC_EXPLODE;

    private final Random rng = new Random();

    public FallingItemSmashAbility(
            @Nonnull WarlordsNPC source,
            @Nonnull Supplier<Location> centerSupplier,
            // targeting
            int count,
            double areaHalfSizeX,
            double areaHalfSizeZ,
            // fall & timing
            double spawnHeight,
            int fallTicks,
            int lingerTicks,
            // impact
            double impactRadius,
            float damageMin,
            float damageMax,
            boolean applySlow,
            int slowTicks,
            int slowPercent,
            boolean knockback,
            double knockbackStrength,
            // display visuals
            @Nonnull Material displayMaterial,
            double scale,
            boolean glow
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        this.count = Math.max(1, count);
        this.areaHalfSizeX = Math.max(0.0, areaHalfSizeX);
        this.areaHalfSizeZ = Math.max(0.0, areaHalfSizeZ);

        this.spawnHeight = Math.max(2.0, spawnHeight);
        this.fallTicks = Math.max(5, fallTicks);
        this.lingerTicks = Math.max(5, lingerTicks);

        this.impactRadius = Math.max(0.5, impactRadius);
        this.damageMin = Math.max(0f, damageMin);
        this.damageMax = Math.max(this.damageMin, damageMax);
        this.applySlow = applySlow;
        this.slowTicks = Math.max(0, slowTicks);
        this.slowPercent = Math.min(Math.max(slowPercent, 0), 100);
        this.knockback = knockback;
        this.knockbackStrength = Math.max(0.0, knockbackStrength);

        this.displayMaterial = displayMaterial;
        this.scale = Math.max(0.1, scale);
        this.glow = glow;
    }

    /** Cast once: spawns N falling displays around the center. */
    public void cast() {
        final Location center = groundSnap(centerSupplier.get().clone());
        if (center.getWorld() == null) return;

        center.getWorld().playSound(center, spawnSfx, 1f, 1.0f);

        List<Location> targets = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            double ox = (rng.nextDouble() * 2 - 1) * areaHalfSizeX;
            double oz = (rng.nextDouble() * 2 - 1) * areaHalfSizeZ;
            Location at = new Location(center.getWorld(), center.getX() + ox, center.getY(), center.getZ() + oz);
            targets.add(groundSnap(at));
        }

        for (Location target : targets) {
            // optional telegraph ring
            drawRingDust(target, impactRadius, 20, telegraphDust);
            spawnFallingDisplay(target);
        }
    }

    /** Also expose a direct-cast variant at a specific world position (e.g., under a player). */
    public void castAt(@Nonnull Location target) {
        Location t = groundSnap(target.clone());
        drawRingDust(t, impactRadius, 20, telegraphDust);
        spawnFallingDisplay(t);
    }

    private void spawnFallingDisplay(@NotNull Location target) {
        final Location from = target.clone().add(0, spawnHeight, 0);
        final ItemDisplay display = spawnItemDisplay(from, displayMaterial, (float) scale, glow, true);

        new GameRunnable(source.getGame()) {
            int t = 0;

            @Override
            public void run() {
                if (t == 0) {
                    target.getWorld().playSound(from, fallWhooshSfx, 0.7f, 1.2f);
                }

                if (t >= fallTicks) {
                    // Impact: rotate to lie flat and deal AoE
                    layFlat(display);
                    doImpact(target);

                    // linger then cleanup
                    new GameRunnable(source.getGame()) {
                        int life = lingerTicks;

                        @Override
                        public void run() {
                            if (life-- <= 0 || display.isDead()) {
                                if (!display.isDead()) display.remove();
                                this.cancel();
                            } else {
                                // small idle heart to highlight the stuck blade
                                target.getWorld().spawnParticle(heart, target.clone().add(0, .2, 0), 1, .05, .05, .05, 0);
                            }
                        }
                    }.runTaskTimer(0, 1);

                    this.cancel();
                    return;
                }

                double prog = (double) t / fallTicks; // 0 -> 1
                double y = from.getY() + (target.getY() - from.getY()) * prog;
                Location mid = new Location(target.getWorld(), target.getX(), y, target.getZ());

                if (!display.isDead()) {
                    display.teleport(mid);
                }

                // falling trail
                target.getWorld().spawnParticle(fallTrail, mid, 3, .1, .1, .1, 0);
                t++;
            }
        }.runTaskTimer(0, 1);
    }

    private void doImpact(Location at) {
        at.getWorld().playSound(at, impactSfx, 0.9f, 1.0f);
        at.getWorld().spawnParticle(impactPop, at, 30, 0.6, 0.3, 0.6, 0.05, popBlock);

        PlayerFilter.entitiesAround(at, impactRadius, 3, impactRadius)
                .aliveEnemiesOf(source)
                .forEach(wp -> {
                    wp.addInstance(InstanceBuilder
                            .damage()
                            .cause("Sweeping Death")
                            .source(source)
                            .min(damageMin)
                            .max(damageMax)
                    );

                    if (applySlow && slowPercent > 0 && slowTicks > 0) {
                        wp.addSpeedModifier(source, "Falling Item Smash", -slowPercent, slowTicks);
                    }

                    if (knockback && knockbackStrength > 0) {
                        Utils.addKnockback("Smash Ability", at, wp, knockbackStrength, 0.2f);
                    }
                });
    }

    // --- display helpers ---

    /**
     * Spawns an ItemDisplay at 'at'.
     * If tipDown is true, rotate so it points downward (as if falling tip-first).
     */
    private static ItemDisplay spawnItemDisplay(@NotNull Location at, Material mat, float uniformScale, boolean glow, boolean tipDown) {
        return at.getWorld().spawn(at, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(mat));
            d.setBillboard(Billboard.FIXED);
            d.setPersistent(true);
            d.setInvulnerable(true);
            d.setGlowing(glow);

            float s = Math.max(0.1f, uniformScale);
            Quaternionf left = new Quaternionf();
            Quaternionf right = new Quaternionf();

            // Default orientation: Minecraft items are flat; rotate so the "blade" looks vertical.
            // We'll use the right rotation to orient the model.
            if (tipDown) {
                // Rotate 90° around X to stand it up, then 180° around Z to point tip downward
                right.rotateX((float) Math.toRadians(90));
                right.rotateZ((float) Math.toRadians(180));
            }

            d.setTransformation(new Transformation(
                    new Vector3f(0f, 0f, 0f), // translation
                    left,                     // left rotation
                    new Vector3f(s, s, s),    // scale
                    right                     // right rotation
            ));
        });
    }

    /** Rotate the display to lie flat (90° onto the ground). */
    private static void layFlat(@NotNull ItemDisplay display) {
        try {
            Transformation t = display.getTransformation();
            Quaternionf right = new Quaternionf(t.getRightRotation());
            // Rotate -90° around X to lie the blade on the ground
            right.rotateX((float) Math.toRadians(-90));

            display.setTransformation(new Transformation(
                    new Vector3f(t.getTranslation()),
                    new Quaternionf(t.getLeftRotation()),
                    new Vector3f(t.getScale()),
                    right
            ));
        } catch (Throwable ignored) {
        }
    }

    // --- small utils ---

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
}
