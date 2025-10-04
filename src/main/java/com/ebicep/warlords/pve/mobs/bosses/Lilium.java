package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.LiliathEngima;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NineCrystal;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class Lilium extends AbstractMob implements BossMob {

    private Location mapCenter;

    private OrbitingItemManager oribitingItemManagerFloating;

    private BouquetBarrageAbility bouquetBarrageAbility;
    private RoseGardenAbility roseGardenAbility;
    private PetalStormAbility petalStormAbility;

    private CrystalConduitsAbility conduitsOne;
    private CrystalConduitsAbility conduitsTwo;
    private CrystalConduitsAbility conduitsThree;
    private CrystalConduitsAbility conduitsFour;
    private CrystalConduitsAbility conduitsFive;

    private SkyPlatformsController platformsController;

    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private BossAbilityPhase phaseFour;
    private BossAbilityPhase phaseFive;
    private BossAbilityPhase phaseSix;
    private BossAbilityPhase phaseSeven;
    private BossAbilityPhase phaseEight;

    private boolean preventDashing = false;

    public Lilium(Location spawnLocation) {
        super(spawnLocation,
                "Lilium",
                280000,
                0.36f,
                30,
                600,
                900
        );
    }

    public Lilium(
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
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ALLAY_DEATH, 500, 0.5f);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);

        oribitingItemManagerFloating = new OrbitingItemManager(() -> warlordsNPC.getLocation().clone().add(0, 8, 0), 33, 3, 1, 15, option, warlordsNPC, Material.STICK);

        bouquetBarrageAbility = new BouquetBarrageAbility(warlordsNPC, 3, 20, 40, 4, 2000, 3000, true, 40, 20);
        roseGardenAbility = new RoseGardenAbility(
                warlordsNPC,
                () -> mapCenter,
                8,
                22,
                2,
                6,
                40,
                200,
                200,
                2000,
                3000,
                true,
                40,
                30,
                true,
                6,
                2000,
                3000,
                true,
                Material.CRIMSON_FUNGUS,
                8,
                false,
                5,
                1
        );

        petalStormAbility = new PetalStormAbility(
                warlordsNPC,
                () -> mapCenter,
                8, 8, 30, 25, 12, 22.0, 22.0, 8.0,
                3.25, 1500f, 2100, true, 40, true, 30, 25,
                true, 'X', 5, "ALTERNATING", 1
        );

        platformsController = new SkyPlatformsController(
                warlordsNPC,
                () -> mapCenter,
                List.of(230, 190, 150, 110, 60),
                List.of(32.0, 32.0, 32.0, 32.0, 32.0),
                Material.CHERRY_LEAVES,
                true,
                2,
                40,
                6,
                true
        );

        conduitsOne = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                2,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsTwo = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                2,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsThree = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                2,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsFour = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                2,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsFive = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                2,
                18,
                2,
                0.05,
                4000,
                option
        );

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            petalStormAbility.cast();
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 80, () -> {
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    Location crystalLoc = mapCenter.clone().add(0, 1, 0);
                    if (t++ < 9) {
                        double angle = t / 9D * Math.PI * 2;
                        crystalLoc.setX(mapCenter.getX() + Math.sin(angle) * 25);
                        crystalLoc.setZ(mapCenter.getZ() + cos(angle) * 25);
                        LiliathEngima crystal = new LiliathEngima(crystalLoc, PlayerFilter.playingGame(warlordsNPC.getGame())
                                .aliveEnemiesOf(warlordsNPC)
                                .stream().toList()
                        );
                        pveOption.spawnNewMob(crystal, Team.RED);
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                    }

                    if (t == 9) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(40, 6);

        });

        phaseThree = new BossAbilityPhase(warlordsNPC, 70, () -> {
            PlayerFilter.playingGame(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(enemy -> {
                        enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Mirror Effect",
                                "MIRROR",
                                MirrorDPSPhaseData.class,
                                null,
                                warlordsNPC,
                                CooldownTypes.FIELD_EFFECT,
                                cooldownManager -> {},
                                30 * 20
                        ) {
                            @Override
                            public void onHealFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                event.setCancelled(true);
                                enemy.addInstance(InstanceBuilder.damage()
                                        .source(event.getSource())
                                        .cause(event.getCause())
                                        .min(event.getMin())
                                        .max(event.getMax())
                                        .critChance(event.getCritChance())
                                        .critMultiplier(event.getCritMultiplier())
                                        .flags(event.getFlags())
                                );
                            }

                            @Override
                            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                event.setCancelled(true);
                                event.getWarlordsEntity().addInstance(InstanceBuilder.healing()
                                        .source(enemy)
                                        .cause(event.getCause())
                                        .min(event.getMin())
                                        .max(event.getMax())
                                        .critChance(event.getCritChance())
                                        .critMultiplier(event.getCritMultiplier())
                                        .flags(event.getFlags())
                                );
                            }
                        });
                    }
            );
        });

        phaseFour = new BossAbilityPhase(warlordsNPC, 60, () -> {
            preventDashing = true;
            warlordsNPC.teleport(mapCenter.clone().add(0, 40, 0));
            warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 60, false));

            for (WarlordsEntity enemy : PlayerFilter
                    .playingGame(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                enemy.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 80, false));
            }

            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    for (WarlordsEntity enemy : PlayerFilter
                            .playingGame(warlordsNPC.getGame())
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        EffectUtils.strikeLightning(enemy.getLocation(), false);
                    }

                    warlordsNPC.setStunTicks(1000);

                    conduitsOne.setFollowBoss(true);
                    conduitsOne.setFollowLerp(1);
                    conduitsOne.start(warlordsNPC.getGame());

                    oribitingItemManagerFloating.spawnSwords(9);
                    oribitingItemManagerFloating.start();

                    platformsController.start(warlordsNPC.getGame());
                }
            }.runTaskLater(80);

            new GameRunnable(warlordsNPC.getGame()) {
                boolean oneTriggered = false;
                boolean twoTriggered = false;
                boolean threeTriggered = false;
                boolean fourTriggered = false;
                boolean fiveTriggered = false;
                @Override
                public void run() {
                    if (conduitsOne.isCompleted() && !oneTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        new GameRunnable(warlordsNPC.getGame()) {
                            @Override
                            public void run() {
                                conduitsTwo.setFollowBoss(true);
                                conduitsTwo.start(warlordsNPC.getGame());
                            }
                        }.runTaskLater(30);
                        oneTriggered = true;
                    }
                    if (conduitsTwo.isCompleted() && !twoTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        new GameRunnable(warlordsNPC.getGame()) {
                            @Override
                            public void run() {
                                conduitsThree.setFollowBoss(true);
                                conduitsThree.start(warlordsNPC.getGame());
                            }
                        }.runTaskLater(30);
                        twoTriggered = true;
                    }
                    if (conduitsThree.isCompleted() && !threeTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        new GameRunnable(warlordsNPC.getGame()) {
                            @Override
                            public void run() {
                                conduitsFour.setFollowBoss(true);
                                conduitsFour.start(warlordsNPC.getGame());
                            }
                        }.runTaskLater(30);
                        threeTriggered = true;
                    }
                    if (conduitsFour.isCompleted() && !fourTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        new GameRunnable(warlordsNPC.getGame()) {
                            @Override
                            public void run() {
                                conduitsFive.setFollowBoss(true);
                                conduitsFive.start(warlordsNPC.getGame());
                            }
                        }.runTaskLater(30);
                        fourTriggered = true;
                    }
                    if (conduitsFive.isCompleted() && !fiveTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        fiveTriggered = true;
                    }

                    if (warlordsNPC.getLocation().getY() < 45) {
                        oribitingItemManagerFloating.stop();

                        platformsController.stop();

                        warlordsNPC.teleport(option.getRandomSpawnLocation(warlordsNPC));
                        for (WarlordsEntity player : PlayerFilter
                                .playingGame(warlordsNPC.getGame())
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            player.removePotionEffect(PotionEffectType.LEVITATION);
                            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
                            player.teleport(option.getRandomSpawnLocation(player));
                        }

                        preventDashing = false;
                        warlordsNPC.setStunTicks(0);
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);

        });

        phaseFive = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseSix = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseSeven = new BossAbilityPhase(warlordsNPC, 70, () -> {});
        phaseEight = new BossAbilityPhase(warlordsNPC, 70, () -> {});
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        EffectUtils.playCircularEffectAround(warlordsNPC.getGame(), warlordsNPC.getLocation(), Particle.CHERRY_LEAVES, 1, 1.3, 0.1, 2.2, 8, 1, 4, 20);

        if (ticksElapsed % 400 == 0) {
            roseGardenAbility.cast();
        }

        if (ticksElapsed % 320 == 0) {
            bouquetBarrageAbility.cast();
        }

        if (ticksElapsed % 480 == 0 && ticksElapsed > 0) {
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;
                    bladeWaltsAbility(warlordsNPC);
                    dropBouquetNode(warlordsNPC.getLocation());
                    if (t == 3) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 10);
        }

        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
        phaseThree.initialize(health);
        phaseFour.initialize(health);
        phaseFive.initialize(health);
        phaseSix.initialize(health);
        phaseSeven.initialize(health);
        phaseEight.initialize(health);
    }

    private void bladeWaltsAbility(WarlordsNPC warlordsNPC) {
        Set<WarlordsEntity> hit = new HashSet<>();
        LocationBuilder locationBuilder = new LocationBuilder(warlordsNPC.getEyeLocation());
        for (Block ignored : Utils.getTargetBlockInBetween(warlordsNPC.getEyeLocation(), 9)) {
            if (!Utils.getTargetBlock(locationBuilder, 1).getType().isAir() ||
                    !locationBuilder.getBlock().getType().isAir() ||
                    !locationBuilder.clone()
                            .addY(1)
                            .getBlock()
                            .getType()
                            .isAir()
            ) {
                locationBuilder.centerXZBlock();
                boolean isSlab = locationBuilder.clone().addY(-1).getBlock().getBlockData() instanceof Slab;
                locationBuilder.addY(isSlab ? -0.5 : 0);
                break;
            }
            PlayerFilter.entitiesAround(locationBuilder.clone().addY(-1), 2, 2, 2)
                    .aliveEnemiesOf(warlordsNPC)
                    .excluding(hit)
                    .forEach(warlordsEntity -> {
                        hit.add(warlordsEntity);
                        warlordsEntity.addInstance(InstanceBuilder
                                .damage()
                                .cause("Waltz")
                                .source(warlordsNPC)
                                .min(1200)
                                .max(1800)
                        );
            });
            locationBuilder = locationBuilder.forward(1);
            EffectUtils.displayParticle(Particle.CHERRY_LEAVES, locationBuilder.clone().addY(-.5), 10, .1, .1, .1, 0);
        }
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ALLAY_DEATH, 500, 0.5f);
        warlordsNPC.teleportLocationOnly(locationBuilder);
    }

    private void dropBouquetNode(Location at) {
        Particle.DustOptions ringDust = new Particle.DustOptions(Color.fromRGB(255, 105, 180), 1.05f);
        final Set<UUID> hitOnce = new HashSet<>();
        final double maxR = 3;

        new GameRunnable(warlordsNPC.getGame()) {
            double r = 1;
            int life = 20;

            @Override
            public void run() {
                if (life-- <= 0 || r > maxR) {
                    // small finish pop
                    at.getWorld().spawnParticle(Particle.CHERRY_LEAVES, at, 10, 0.3, 0.15, 0.3, 0.0);
                    at.getWorld().spawnParticle(Particle.HEART, at, 18, 0.5, 0.25, 0.5, 0.05);
                    this.cancel();
                    return;
                }

                // ring visuals (approximate circle)
                drawRingDust(at, r, 24, ringDust);
                if (life % 6 == 0) {
                    at.getWorld().playSound(at, Sound.BLOCK_BEACON_ACTIVATE, 2, 0.5f);
                }

                PlayerFilter.entitiesAround(at, r, 3, r)
                        .aliveEnemiesOf(warlordsNPC)
                        .forEach(wp -> {
                            UUID id = wp.getUuid();
                            if (!hitOnce.add(id)) return; // already hit by this bouquet
                            wp.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Waltz Bloom")
                                    .source(warlordsNPC)
                                    .min(800)
                                    .max(1200)
                            );
                            Utils.addKnockback("Lilium Knockback", warlordsNPC.getLocation(), wp, -1.15, 0.2);
                        });

                r += 0.1;
            }
        }.runTaskTimer(0, 1);
    }

    private static void drawRingDust(Location center, double radius, int points, Particle.DustOptions dust) {
        final double step = (Math.PI * 2) / points;
        for (int i = 0; i < points; i++) {
            double angle = i * step;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            Location p = new Location(center.getWorld(), x, center.getY(), z);
            center.getWorld().spawnParticle(Particle.DUST, p, 1, dust);
        }
    }

    private static class MirrorDPSPhaseData {

    }

    @Override
    public TextColor getColor() {
        return TextColor.color(255, 192, 203);
    }

    @Override
    public Component getDescription() {
        return Component.text("Queen of Hearts", TextColor.color(218, 112, 214));
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.LILIUM;
    }
}
