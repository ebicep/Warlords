package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;

public class Veilkeeper extends AbstractMob implements BossMob {

    private Location mapCenter;
    private OrbitingItemManager oribitingItemManager;
    private OrbitingItemManager oribitingItemManagerUp;
    private OrbitingItemManager oribitingItemManagerFloating;
    private PairedSequenceAbility pairedSequenceAbility;
    private ShatteringChainsAbility fallingShatterChainsOne;

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

    public Veilkeeper(Location spawnLocation) {
        super(spawnLocation,
                "Veilkeeper",
                320000,
                0,
                30,
                1200,
                2000
        );
    }

    public Veilkeeper(
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

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);

        oribitingItemManager = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 4, 1, 6, 2, option, warlordsNPC, Material.CRIMSON_ROOTS);
        oribitingItemManagerUp = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 4, 3, 6, 2, option, warlordsNPC, Material.CRIMSON_ROOTS);
        oribitingItemManagerFloating = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 33, 3, 1, 15, option, warlordsNPC, Material.FROGSPAWN);

        oribitingItemManager.spawnSwords(12);
        oribitingItemManagerUp.spawnSwords(12);
        oribitingItemManager.start();
        oribitingItemManagerUp.start();

        pairedSequenceAbility = new PairedSequenceAbility(
                warlordsNPC,
                () -> mapCenter,
                List.of(
                        PairedSequenceAbility.SanctumColor.BLUE,
                        PairedSequenceAbility.SanctumColor.GREEN,
                        PairedSequenceAbility.SanctumColor.RED,
                        PairedSequenceAbility.SanctumColor.YELLOW
                ),
                4,
                35,
                5,
                200,
                200,
                100,
                200,
                2,
                5000,
                25000,
                amt -> warlordsNPC.addInstance(InstanceBuilder.healing().value(25000).source(warlordsNPC).cause("Sequence Fail")
        ));

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

        fallingShatterChainsOne = new ShatteringChainsAbility(warlordsNPC, () -> mapCenter.clone().add(0, 130, 0), 12, 35, 100, 200, 5, 1.5, 2, 2, 3000);

        platformsController = new SkyPlatformsController(
                warlordsNPC,
                () -> mapCenter,
                List.of(230, 190, 150, 110, 60),
                List.of(24.0, 24.0, 24.0, 24.0, 24.0),
                Material.GILDED_BLACKSTONE,
                true,
                2,
                40,
                6,
                true
        );

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            warlordsNPC.teleport(mapCenter.add(0, 40, 0));
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

                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);

        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 70, () -> {
            pairedSequenceAbility.start(warlordsNPC.getGame());
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
    }

    boolean triggered = false;

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!triggered) {
            triggered = true;
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        oribitingItemManager.stop();
        oribitingItemManagerUp.stop();
        platformsController.stop();
        conduitsOne.stop();
    }

    @Override
    public Component getDescription() {
        return Component.text("The Commandment of Unrivaled Chains", NamedTextColor.RED);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_RED;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VEILKEEPER;
    }
}
