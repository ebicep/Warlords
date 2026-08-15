package com.ebicep.warlords.pve.mobs.bosses.raidbosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.RaidBossMob;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RaidMithra extends AbstractMob implements RaidBossMob {

    private static final int CRYSTAL_COUNT = 6;
    private static final double CRYSTAL_ORBIT_RADIUS = 1.2;
    private static final double CRYSTAL_ORBIT_SPEED = 0.75;
    private static final double CRYSTAL_VERTICAL_AMPLITUDE = 0.45;
    private static final Particle.DustOptions WHITE_DUST = new Particle.DustOptions(Color.fromRGB(245, 245, 255), 1.25f);
    private static final Particle.DustOptions ABYSS_DUST = new Particle.DustOptions(Color.fromRGB(88, 52, 130), 1.25f);

    private final List<ItemDisplay> orbitingCrystals = new ArrayList<>();
    private ItemDisplay royalCrown;
    private RaidBossUtils.RaidBossHealthBar raidHealthBar;
    private Location previousLocation;
    private float crownRotation;
    private int attackAnimationTicks;
    private int chessStep;

    public RaidMithra(Location spawnLocation) {
        this(spawnLocation, "Mithra", 4_000_000, 0.35f, 20, 1200, 1600);
    }

    public RaidMithra(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.RAID_MITHRA;
    }

    @Override
    public Component getDescription() {
        return Component.text("♦ RAID BOSS ♦", TextColor.color(225, 85, 115), TextDecoration.BOLD);
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(105, 225, 255);
    }

    @Override
    public double getMobScale() {
        return 1.4;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        spawnRoyalCrown(warlordsNPC);
        spawnOrbitingCrystals();
        raidHealthBar = RaidBossUtils.createHealthBar(
                warlordsNPC,
                0.9f,
                1.45,
                "MITHRA",
                getDescription(),
                NamedTextColor.RED
        );
        playQueenMoveSet(warlordsNPC.getLocation());

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WITHER_SPAWN, 3, 0.65f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 3, 0.75f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 3, 0.5f);
        EffectUtils.playFirework(
                warlordsNPC.getLocation().clone().add(0, 2, 0),
                FireworkEffect.builder()
                              .withColor(Color.WHITE, Color.fromRGB(90, 45, 135), Color.BLACK)
                              .with(FireworkEffect.Type.BALL_LARGE)
                              .withTrail()
                              .build()
        );

        previousLocation = warlordsNPC.getLocation().clone();
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location current = warlordsNPC.getLocation();
        boolean moving = isMoving(current);

        updateRoyalCrown(current, ticksElapsed, moving);
        updateOrbitingCrystals(ticksElapsed);
        if (raidHealthBar != null) {
            raidHealthBar.update();
        }

        if (ticksElapsed % 30 == 0 && !moving) {
            playQueenMoveSet(current);
        }

        if (ticksElapsed % 5 == 0 && moving) {
            playChessStep(current);
        }

        if (ticksElapsed % 8 == 0) {
            current.getWorld().spawnParticle(
                    Particle.END_ROD,
                    current.clone().add(0, 3.5, 0),
                    4,
                    1.4,
                    1.8,
                    1.4,
                    0
            );
        }

        if (ticksElapsed % 120 == 0) {
            Utils.playGlobalSound(current, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.2f, 0.55f);
        }

        if (attackAnimationTicks > 0) {
            attackAnimationTicks--;
        }

        previousLocation = current.clone();
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        attackAnimationTicks = 12;
        playRoyalSlash(receiver);

        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2, 0.55f);
        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.25f, 1.65f);
        Utils.playGlobalSound(receiver.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.5f, 0.8f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);

        EffectUtils.playFirework(
                deathLocation.clone().add(0, 2, 0),
                FireworkEffect.builder()
                              .withColor(Color.WHITE, Color.fromRGB(90, 45, 135), Color.BLACK)
                              .with(FireworkEffect.Type.STAR)
                              .withTrail()
                              .withFlicker()
                              .build()
        );
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_WITHER_DEATH, 3, 0.7f);
        Utils.playGlobalSound(deathLocation, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 3, 0.45f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        if (royalCrown != null && !royalCrown.isDead()) {
            royalCrown.remove();
        }
        for (ItemDisplay crystal : orbitingCrystals) {
            if (crystal != null && !crystal.isDead()) {
                crystal.remove();
            }
        }
        orbitingCrystals.clear();
        if (raidHealthBar != null) {
            raidHealthBar.remove();
        }
        royalCrown = null;
        raidHealthBar = null;
        previousLocation = null;
    }

    private void spawnRoyalCrown(WarlordsEntity we) {
        ItemStack crownItem = Weapons.WARLORDS_II_ROYAL_CHAKRAM.getItem().clone();
        Location location = warlordsNPC.getLocation().clone().add(0, we.getEntity().getHeight() + 0.75, 0);

        royalCrown = warlordsNPC.getWorld().spawn(location, ItemDisplay.class, display -> {
            display.setItemStack(crownItem);
            display.setBillboard(Display.Billboard.FIXED);
            display.setInterpolationDuration(1);
            display.setPersistent(false);
            display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf().rotateX((float) Math.toRadians(90)),
                    new Vector3f(2.4f, 2.4f, 2.4f),
                    new Quaternionf()
            ));
        });
    }

    private void updateRoyalCrown(Location location, int ticksElapsed, boolean moving) {
        if (royalCrown == null || royalCrown.isDead()) {
            return;
        }

        LivingEntity entity = (LivingEntity) warlordsNPC.getEntity();
        double bob = Math.sin(ticksElapsed * 0.09) * 0.18;
        royalCrown.teleport(location.clone().add(0, entity.getHeight() + 0.75 + bob, 0));

        crownRotation += attackAnimationTicks > 0 ? 20 : moving ? 5 : 1.75f;
        float scale = attackAnimationTicks > 0 ? 2.8f : 2.4f;
        float attackTilt = attackAnimationTicks > 0 ? 18 : 0;

        royalCrown.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf()
                        .rotateX((float) Math.toRadians(90 + attackTilt))
                        .rotateZ((float) Math.toRadians(crownRotation)),
                new Vector3f(scale, scale, scale),
                new Quaternionf()
        ));
    }

    private void spawnOrbitingCrystals() {
        ItemStack crystalItem = new ItemStack(Material.AMETHYST_SHARD);
        Location spawn = warlordsNPC.getLocation();

        for (int i = 0; i < CRYSTAL_COUNT; i++) {
            int index = i;
            ItemDisplay crystal = warlordsNPC.getWorld().spawn(spawn, ItemDisplay.class, display -> {
                float scale = 0.54f + index % 3 * 0.05f;
                display.setItemStack(crystalItem.clone());
                display.setBillboard(Display.Billboard.FIXED);
                display.setInterpolationDuration(2);
                display.setTeleportDuration(2);
                display.setPersistent(false);
                display.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf()
                                .rotateX((float) Math.toRadians(55))
                                .rotateZ((float) Math.toRadians(index * (360d / CRYSTAL_COUNT))),
                        new Vector3f(scale, scale, scale),
                        new Quaternionf()
                ));
            });
            orbitingCrystals.add(crystal);
        }

        updateOrbitingCrystals(0);
    }

    private void updateOrbitingCrystals(int ticksElapsed) {
        if (!(warlordsNPC.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        double centerX = (entity.getBoundingBox().getMinX() + entity.getBoundingBox().getMaxX()) / 2;
        double centerZ = (entity.getBoundingBox().getMinZ() + entity.getBoundingBox().getMaxZ()) / 2;
        double height = entity.getBoundingBox().getMaxY() - entity.getBoundingBox().getMinY();
        double centerY = entity.getBoundingBox().getMinY() + height * 0.52;
        double baseAngle = Math.toRadians(ticksElapsed * CRYSTAL_ORBIT_SPEED);

        for (int i = 0; i < orbitingCrystals.size(); i++) {
            ItemDisplay crystal = orbitingCrystals.get(i);
            if (crystal == null || crystal.isDead()) {
                continue;
            }

            double phase = Math.PI * 2 * i / CRYSTAL_COUNT;
            double angle = baseAngle + phase;
            double radius = CRYSTAL_ORBIT_RADIUS + Math.sin(angle * 3 + phase) * 0.08;
            double y = centerY + Math.sin(angle * 2 + phase * 0.5) * CRYSTAL_VERTICAL_AMPLITUDE;

            crystal.teleport(new Location(
                    entity.getWorld(),
                    centerX + Math.cos(angle) * radius,
                    y,
                    centerZ + Math.sin(angle) * radius
            ));

            float scale = 0.54f + i % 3 * 0.05f;
            float spin = (float) Math.toRadians(ticksElapsed * 1.25 + i * 45);
            crystal.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf()
                            .rotateX((float) Math.toRadians(55))
                            .rotateY(spin)
                            .rotateZ((float) angle),
                    new Vector3f(scale, scale, scale),
                    new Quaternionf()
            ));
        }
    }

    private boolean isMoving(Location current) {
        if (previousLocation == null || previousLocation.getWorld() != current.getWorld()) {
            return false;
        }

        double x = current.getX() - previousLocation.getX();
        double z = current.getZ() - previousLocation.getZ();
        return x * x + z * z > 0.0025;
    }

    private void playQueenMoveSet(Location center) {
        World world = center.getWorld();
        Location origin = center.clone().add(0, 0.12, 0);
        Vector[] directions = {
                new Vector(1, 0, 0),
                new Vector(-1, 0, 0),
                new Vector(0, 0, 1),
                new Vector(0, 0, -1),
                new Vector(1, 0, 1).normalize(),
                new Vector(-1, 0, 1).normalize(),
                new Vector(1, 0, -1).normalize(),
                new Vector(-1, 0, -1).normalize()
        };

        for (int directionIndex = 0; directionIndex < directions.length; directionIndex++) {
            Vector direction = directions[directionIndex];
            Particle.DustOptions dust = directionIndex % 2 == 0 ? WHITE_DUST : ABYSS_DUST;

            for (double distance = 0.75; distance <= 7.5; distance += 0.55) {
                Location point = origin.clone().add(direction.clone().multiply(distance));
                world.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, dust);
            }

            Location endpoint = origin.clone().add(direction.clone().multiply(7.5));
            world.spawnParticle(Particle.END_ROD, endpoint.clone().add(0, 0.2, 0), 1, 0, 0, 0, 0);
        }
    }

    private void playChessStep(Location center) {
        World world = center.getWorld();
        Particle.DustOptions dust = chessStep++ % 2 == 0 ? WHITE_DUST : ABYSS_DUST;
        Location origin = center.clone().add(0, 0.08, 0);
        double half = 0.75;

        for (double offset = -half; offset <= half; offset += 0.3) {
            world.spawnParticle(Particle.DUST, origin.clone().add(offset, 0, half), 1, 0, 0, 0, 0, dust);
            world.spawnParticle(Particle.DUST, origin.clone().add(offset, 0, -half), 1, 0, 0, 0, 0, dust);
            world.spawnParticle(Particle.DUST, origin.clone().add(half, 0, offset), 1, 0, 0, 0, 0, dust);
            world.spawnParticle(Particle.DUST, origin.clone().add(-half, 0, offset), 1, 0, 0, 0, 0, dust);
        }
    }

    private void playRoyalSlash(WarlordsEntity receiver) {
        Location center = receiver.getLocation().clone().add(0, 1.1, 0);
        World world = center.getWorld();

        Vector forward = center.toVector().subtract(warlordsNPC.getLocation().toVector()).setY(0);
        if (forward.lengthSquared() < 0.001) {
            forward = new Vector(0, 0, 1);
        }
        forward.normalize();
        Vector right = new Vector(-forward.getZ(), 0, forward.getX());

        for (int i = -10; i <= 10; i++) {
            double progress = i / 10d;
            Vector side = right.clone().multiply(progress * 2.4);

            Location firstSlash = center.clone().add(side).add(0, progress * 1.2, 0);
            Location secondSlash = center.clone().add(side).add(0, -progress * 1.2, 0);

            world.spawnParticle(Particle.DUST, firstSlash, 1, 0, 0, 0, 0, WHITE_DUST);
            world.spawnParticle(Particle.DUST, secondSlash, 1, 0, 0, 0, 0, ABYSS_DUST);
        }

        world.spawnParticle(Particle.SWEEP_ATTACK, center, 3, 0.7, 0.7, 0.7, 0);
        world.spawnParticle(Particle.END_ROD, center, 14, 0.8, 0.8, 0.8, 0.02);
        world.spawnParticle(Particle.CRIT, center, 18, 0.9, 0.9, 0.9, 0.12);
    }
}
