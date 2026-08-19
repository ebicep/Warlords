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
    private static final int ATTACK_ANIMATION_DURATION = 18;
    private static final double CRYSTAL_ORBIT_RADIUS = 2;
    private static final double CRYSTAL_ORBIT_SPEED = 0.75;
    private static final double CRYSTAL_VERTICAL_AMPLITUDE = 0.6;
    private static final Particle.DustOptions WHITE_DUST = new Particle.DustOptions(Color.fromRGB(245, 245, 255), 1.25f);
    private static final Particle.DustOptions ABYSS_DUST = new Particle.DustOptions(Color.fromRGB(88, 52, 130), 1.25f);

    private final List<ItemDisplay> orbitingCrystals = new ArrayList<>();
    private final List<ItemDisplay> royalHaloDisplays = new ArrayList<>();
    private RaidBossUtils.RaidBossHealthBar raidHealthBar;
    private Location previousLocation;
    private Location royalAttackImpact;
    private Vector royalAttackForward;
    private float haloRotation;
    private int attackAnimationTicks;
    private int royalAttackSequenceTicks = -1;
    private int chessStep;

    public RaidMithra(Location spawnLocation) {
        this(spawnLocation, "Mithra", 4_000_000, 0.18f, 20, 1200, 1600);
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
        return 2;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        spawnRoyalHalo();
        spawnOrbitingCrystals();
        raidHealthBar = RaidBossUtils.createHealthBar(
                warlordsNPC,
                1.3f,
                this.getMobScale() + 0.5,
                getName(),
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

        updateRoyalHalo(ticksElapsed, moving);
        updateOrbitingCrystals(ticksElapsed);
        updateRoyalAttackSequence();
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
        attackAnimationTicks = ATTACK_ANIMATION_DURATION;
        startRoyalAttackSequence(receiver);
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
        for (ItemDisplay display : royalHaloDisplays) {
            if (display != null && !display.isDead()) {
                display.remove();
            }
        }
        royalHaloDisplays.clear();
        for (ItemDisplay crystal : orbitingCrystals) {
            if (crystal != null && !crystal.isDead()) {
                crystal.remove();
            }
        }
        orbitingCrystals.clear();
        if (raidHealthBar != null) {
            raidHealthBar.remove();
        }
        raidHealthBar = null;
        royalAttackImpact = null;
        royalAttackForward = null;
        royalAttackSequenceTicks = -1;
        previousLocation = null;
    }

    private void spawnRoyalHalo() {
        ItemStack chakram = Weapons.WARLORDS_II_ROYAL_CHAKRAM.getItem().clone();
        ItemStack royalJewel = new ItemStack(Material.NETHER_STAR);
        Location location = warlordsNPC.getLocation();

        royalHaloDisplays.add(spawnRoyalHaloDisplay(chakram.clone(), location, 2.7f));
        royalHaloDisplays.add(spawnRoyalHaloDisplay(chakram.clone(), location, 1.35f));
        royalHaloDisplays.add(spawnRoyalHaloDisplay(chakram.clone(), location, 1.35f));
        royalHaloDisplays.add(spawnRoyalHaloDisplay(royalJewel, location, 0.85f));

        updateRoyalHalo(0, false);
    }

    private ItemDisplay spawnRoyalHaloDisplay(ItemStack item, Location location, float scale) {
        return warlordsNPC.getWorld().spawn(location, ItemDisplay.class, display -> {
            display.setItemStack(item);
            display.setBillboard(Display.Billboard.FIXED);
            display.setInterpolationDuration(2);
            display.setTeleportDuration(2);
            display.setPersistent(false);
            display.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),
                    new Vector3f(scale, scale, scale),
                    new Quaternionf()
            ));
        });
    }

    private void updateRoyalHalo(int ticksElapsed, boolean moving) {
        if (royalHaloDisplays.size() < 4 || !(warlordsNPC.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        ItemDisplay mainHalo = royalHaloDisplays.get(0);
        ItemDisplay firstWing = royalHaloDisplays.get(1);
        ItemDisplay secondWing = royalHaloDisplays.get(2);
        ItemDisplay royalJewel = royalHaloDisplays.get(3);
        if (mainHalo.isDead() || firstWing.isDead() || secondWing.isDead() || royalJewel.isDead()) {
            return;
        }

        double centerX = (entity.getBoundingBox().getMinX() + entity.getBoundingBox().getMaxX()) / 2;
        double centerZ = (entity.getBoundingBox().getMinZ() + entity.getBoundingBox().getMaxZ()) / 2;
        double bob = Math.sin(ticksElapsed * 0.09) * 0.16;
        double baseY = entity.getBoundingBox().getMaxY() + 0.72 + bob;
        boolean attacking = attackAnimationTicks > 0;
        double attackPulse = getAttackPulse();

        haloRotation += attacking ? 16 : moving ? 4.5f : 1.5f;
        double rotation = Math.toRadians(haloRotation);
        float attackTilt = (float) (attackPulse * 24);
        float mainScale = (float) (2.7 + attackPulse * 0.9);

        mainHalo.teleport(new Location(entity.getWorld(), centerX, baseY, centerZ));
        mainHalo.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf()
                        .rotateX((float) Math.toRadians(90 + attackTilt))
                        .rotateZ((float) rotation),
                new Vector3f(mainScale, mainScale, mainScale),
                new Quaternionf()
        ));

        double wingRadius = 1.28 + attackPulse * 0.9;
        double wingBob = Math.sin(ticksElapsed * 0.13) * 0.12;
        updateRoyalWing(firstWing, entity, centerX, centerZ, baseY + wingBob, rotation, wingRadius, attackPulse, 1);
        updateRoyalWing(secondWing, entity, centerX, centerZ, baseY - wingBob, rotation + Math.PI, wingRadius, attackPulse, -1);

        double jewelBob = Math.sin(ticksElapsed * 0.16) * 0.12;
        float jewelScale = (float) (0.85 + attackPulse * 0.55);
        royalJewel.teleport(new Location(entity.getWorld(), centerX, baseY + 1.15 + jewelBob + attackPulse * 0.25, centerZ));
        royalJewel.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf()
                        .rotateY((float) -rotation * 1.5f)
                        .rotateZ((float) Math.toRadians(45)),
                new Vector3f(jewelScale, jewelScale, jewelScale),
                new Quaternionf()
        ));

        if (ticksElapsed % 4 == 0) {
            entity.getWorld().spawnParticle(
                    Particle.END_ROD,
                    new Location(entity.getWorld(), centerX, baseY, centerZ),
                    attacking ? 5 : 2,
                    attacking ? 1.4 : 0.8,
                    0.2,
                    attacking ? 1.4 : 0.8,
                    0
            );
        }
    }

    private void updateRoyalWing(
            ItemDisplay wing,
            LivingEntity entity,
            double centerX,
            double centerZ,
            double y,
            double angle,
            double radius,
            double attackPulse,
            int tiltDirection
    ) {
        wing.teleport(new Location(
                entity.getWorld(),
                centerX + Math.cos(angle) * radius,
                y + attackPulse * 0.18,
                centerZ + Math.sin(angle) * radius
        ));

        float scale = (float) (1.35 + attackPulse * 0.5);
        wing.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf()
                        .rotateX((float) Math.toRadians(62 + attackPulse * 12))
                        .rotateY((float) Math.toRadians(tiltDirection * (24 + attackPulse * 16)))
                        .rotateZ((float) angle),
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
                float scale = (float) (getMobScale() + index % 3 * 0.05f);
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

        double attackPulse = getAttackPulse();
        double centerX = (entity.getBoundingBox().getMinX() + entity.getBoundingBox().getMaxX()) / 2;
        double centerZ = (entity.getBoundingBox().getMinZ() + entity.getBoundingBox().getMaxZ()) / 2;
        double height = entity.getBoundingBox().getMaxY() - entity.getBoundingBox().getMinY();
        double centerY = entity.getBoundingBox().getMinY() + height * 0.9 + attackPulse * 0.25;
        double baseAngle = Math.toRadians(ticksElapsed * (CRYSTAL_ORBIT_SPEED + attackPulse * 2.5));

        for (int i = 0; i < orbitingCrystals.size(); i++) {
            ItemDisplay crystal = orbitingCrystals.get(i);
            if (crystal == null || crystal.isDead()) {
                continue;
            }

            double phase = Math.PI * 2 * i / CRYSTAL_COUNT;
            double angle = baseAngle + phase;
            double radius = CRYSTAL_ORBIT_RADIUS + attackPulse * 0.5 + Math.sin(angle * 3 + phase) * 0.08;
            double y = centerY + Math.sin(angle * 2 + phase * 0.5) * (CRYSTAL_VERTICAL_AMPLITUDE + attackPulse * 0.15);

            crystal.teleport(new Location(
                    entity.getWorld(),
                    centerX + Math.cos(angle) * radius,
                    y,
                    centerZ + Math.sin(angle) * radius
            ));

            float scale = (float) (getMobScale() + i % 3 * 0.05f + attackPulse * 0.3);
            float spin = (float) Math.toRadians(ticksElapsed * (1.25 + attackPulse * 3) + i * 45);
            crystal.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf()
                            .rotateX((float) Math.toRadians(55 + attackPulse * 15))
                            .rotateY(spin)
                            .rotateZ((float) angle),
                    new Vector3f(scale, scale, scale),
                    new Quaternionf()
            ));
        }
    }

    private double getAttackPulse() {
        if (attackAnimationTicks <= 0) {
            return 0;
        }
        double progress = 1 - attackAnimationTicks / (double) ATTACK_ANIMATION_DURATION;
        return Math.sin(progress * Math.PI);
    }

    private void startRoyalAttackSequence(WarlordsEntity receiver) {
        royalAttackImpact = receiver.getLocation().clone().add(0, 1.1, 0);
        royalAttackForward = royalAttackImpact.toVector().subtract(warlordsNPC.getLocation().toVector()).setY(0);
        if (royalAttackForward.lengthSquared() < 0.001) {
            royalAttackForward = new Vector(0, 0, 1);
        }
        royalAttackForward.normalize();
        royalAttackSequenceTicks = 0;

        playRoyalWindup();
        playRoyalCleave(royalAttackImpact, royalAttackForward, 0);

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2.2f, 1.35f);
        Utils.playGlobalSound(royalAttackImpact, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 2.5f, 0.45f);
        Utils.playGlobalSound(royalAttackImpact, Sound.ENTITY_WITHER_SHOOT, 1.6f, 1.5f);
    }

    private void updateRoyalAttackSequence() {
        if (royalAttackSequenceTicks < 0 || royalAttackImpact == null || royalAttackForward == null) {
            return;
        }

        royalAttackSequenceTicks++;
        if (royalAttackSequenceTicks == 2) {
            playRoyalCleave(royalAttackImpact, royalAttackForward, Math.PI / 4);
            Utils.playGlobalSound(royalAttackImpact, Sound.ENTITY_PLAYER_ATTACK_CRIT, 2.2f, 0.65f);
            Utils.playGlobalSound(royalAttackImpact, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.8f, 0.7f);
        } else if (royalAttackSequenceTicks == 4) {
            playRoyalImpact(royalAttackImpact, royalAttackForward);
            Utils.playGlobalSound(royalAttackImpact, Sound.ENTITY_WITHER_SHOOT, 2.2f, 0.55f);
            Utils.playGlobalSound(royalAttackImpact, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2.5f, 0.35f);
            Utils.playGlobalSound(royalAttackImpact, Sound.ENTITY_PLAYER_ATTACK_STRONG, 2.5f, 0.5f);
        } else if (royalAttackSequenceTicks >= 6) {
            royalAttackImpact = null;
            royalAttackForward = null;
            royalAttackSequenceTicks = -1;
        }
    }

    private void playRoyalWindup() {
        if (!(warlordsNPC.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        World world = entity.getWorld();
        Location center = new Location(
                world,
                (entity.getBoundingBox().getMinX() + entity.getBoundingBox().getMaxX()) / 2,
                entity.getBoundingBox().getMaxY() + 0.35,
                (entity.getBoundingBox().getMinZ() + entity.getBoundingBox().getMaxZ()) / 2
        );

        for (int i = 0; i < 32; i++) {
            double angle = Math.PI * 2 * i / 32;
            double radius = 2.15;
            Location point = center.clone().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
            world.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, i % 2 == 0 ? WHITE_DUST : ABYSS_DUST);
            if (i % 4 == 0) {
                world.spawnParticle(Particle.END_ROD, point, 1, 0, 0, 0, 0);
            }
        }

        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            Vector direction = new Vector(Math.cos(angle), 0, Math.sin(angle));
            for (double distance = 0.5; distance <= 2.4; distance += 0.45) {
                Location point = center.clone().add(direction.clone().multiply(distance));
                world.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, i % 2 == 0 ? WHITE_DUST : ABYSS_DUST);
            }
        }
    }

    private void playRoyalCleave(Location center, Vector forward, double angleOffset) {
        World world = center.getWorld();
        Vector slashForward = rotateHorizontal(forward, angleOffset);
        Vector right = new Vector(-slashForward.getZ(), 0, slashForward.getX());

        for (int i = -14; i <= 14; i++) {
            double progress = i / 14d;
            Vector side = right.clone().multiply(progress * 3.1);
            Vector bow = slashForward.clone().multiply((1 - Math.abs(progress)) * 0.5);

            Location firstSlash = center.clone().add(side).add(bow).add(0, progress * 1.7, 0);
            Location secondSlash = center.clone().add(side).subtract(bow).add(0, -progress * 1.7, 0);

            world.spawnParticle(Particle.DUST, firstSlash, 1, 0, 0, 0, 0, WHITE_DUST);
            world.spawnParticle(Particle.DUST, secondSlash, 1, 0, 0, 0, 0, ABYSS_DUST);
            if (i % 4 == 0) {
                world.spawnParticle(Particle.END_ROD, firstSlash, 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.END_ROD, secondSlash, 1, 0, 0, 0, 0);
            }
        }

        world.spawnParticle(Particle.SWEEP_ATTACK, center, 4, 0.9, 0.9, 0.9, 0);
        world.spawnParticle(Particle.CRIT, center, 16, 1.1, 1.1, 1.1, 0.15);
    }

    private void playRoyalImpact(Location center, Vector forward) {
        World world = center.getWorld();

        for (int ring = 0; ring < 2; ring++) {
            double radius = ring == 0 ? 1.8 : 3.2;
            for (int i = 0; i < 40; i++) {
                double angle = Math.PI * 2 * i / 40;
                Location point = center.clone().add(Math.cos(angle) * radius, -0.85 + ring * 0.12, Math.sin(angle) * radius);
                world.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, (i + ring) % 2 == 0 ? WHITE_DUST : ABYSS_DUST);
            }
        }

        for (int i = 0; i < 8; i++) {
            Vector direction = rotateHorizontal(forward, Math.PI * 2 * i / 8);
            for (double distance = 0.5; distance <= 3.8; distance += 0.45) {
                Location point = center.clone().add(direction.clone().multiply(distance)).add(0, -0.85, 0);
                world.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, i % 2 == 0 ? WHITE_DUST : ABYSS_DUST);
            }
        }

        world.spawnParticle(Particle.SWEEP_ATTACK, center, 7, 1.3, 1.1, 1.3, 0);
        world.spawnParticle(Particle.END_ROD, center, 28, 1.4, 1.3, 1.4, 0.035);
        world.spawnParticle(Particle.CRIT, center, 32, 1.5, 1.4, 1.5, 0.18);
    }

    private Vector rotateHorizontal(Vector vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector(
                vector.getX() * cos - vector.getZ() * sin,
                0,
                vector.getX() * sin + vector.getZ() * cos
        ).normalize();
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
}
