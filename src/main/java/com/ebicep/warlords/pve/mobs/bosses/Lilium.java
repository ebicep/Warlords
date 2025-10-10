package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.PhysiraCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.CrystallinePetal;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.LiliathEngima;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NineCrystal;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class Lilium extends AbstractMob implements BossMob {

    private Listener listener;
    private Location mapCenter;

    private OrbitingItemManager oribitingItemManagerFloating;
    private OrbitingItemManager oribitingItemManager;
    private LaserBarrageAbility laserBarrageCenter;
    private ArenaShiftAbility arenaShift;

    private BouquetBarrageAbility bouquetBarrageAbility;
    private RoseGardenAbility roseGardenAbility;
    private PetalStormAbility petalStormAbility;
    private OrbitalStrikeAbility orbitalStrikeAbility;
    private HeavenlySpearAbility heavenlySpearAbility;

    private CrystalConduitsAbility conduitsOne;
    private CrystalConduitsAbility conduitsTwo;
    private CrystalConduitsAbility conduitsThree;
    private CrystalConduitsAbility conduitsFour;
    private CrystalConduitsAbility conduitsFive;

    private SkyPlatformsController platformsController;

    private List<UUID> engimas = new ArrayList<>();

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
                0.38f,
                30,
                450,
                600
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
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS, 500, 0.5f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_CONDUIT_ACTIVATE, 500, 0.5f);

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);
        arenaShift = new ArenaShiftAbility(warlordsNPC.getWorld());

        laserBarrageCenter = new LaserBarrageAbility(warlordsNPC.getGame(), warlordsNPC.getLocation(), option.playerCount(), 60, 30, 30, 70, 2, warlordsNPC);

        oribitingItemManager = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 0.5, 0.5, 6, 0.5f, option, warlordsNPC, Material.STICK);
        oribitingItemManager.spawnSwords(6);
        oribitingItemManager.start();
        oribitingItemManagerFloating = new OrbitingItemManager(() -> warlordsNPC.getLocation().clone().add(0, 8, 0), 34, 3, 1, 15, option, warlordsNPC, Material.STICK);

        heavenlySpearAbility = new HeavenlySpearAbility(
                warlordsNPC,
                () -> mapCenter,
                80,
                38,
                10,
                5,
                4000,
                8,
                60,
                2,
                Material.CHERRY_LEAVES,
                Particle.HEART,
                Particle.CHERRY_LEAVES,
                Sound.ENTITY_WARDEN_EMERGE,
                Sound.BLOCK_ANVIL_BREAK
        );

        bouquetBarrageAbility = new BouquetBarrageAbility(warlordsNPC, 3, 20, 40, 4, 2000, 3000, true, 40, 20);

        roseGardenAbility = new RoseGardenAbility(
                warlordsNPC,
                () -> mapCenter,
                8,
                24,
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
                true,
                5,
                1
        );

        petalStormAbility = new PetalStormAbility(
                warlordsNPC,
                () -> mapCenter,
                8, 8, 30, 25, 12, 22.0, 22.0, 8.0,
                3.25, 1500f, 2100, true, 40, true, 30, 25,
                true, 'X', 10, "SEQUENTIAL", 1
        );

        platformsController = new SkyPlatformsController(
                warlordsNPC,
                () -> mapCenter,
                List.of(230, 185, 145, 100, 55),
                List.of(32.0, 32.0, 32.0, 32.0, 32.0),
                Material.STRIPPED_CHERRY_LOG,
                false,
                2,
                40,
                6,
                false
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
                3,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsThree = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                3,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsFour = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                4,
                18,
                2,
                0.05,
                4000,
                option
        );

        conduitsFive = new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                5,
                18,
                2,
                0.05,
                4000,
                option
        );

        orbitalStrikeAbility = new OrbitalStrikeAbility(
                warlordsNPC,
                () -> {
                    WarlordsEntity target = PlayerFilter.playingGame(warlordsNPC.getGame())
                            .aliveEnemiesOf(warlordsNPC)
                            .limit(3)
                            .leastAliveFirst()
                            .findFirstOrNull();
                    return target != null ? target.getLocation().clone() : mapCenter.clone();
                },
                40,
                60,
                20,
                3,
                150,
                64, // maxTrace
                1000, 1500,
                true,
                4,
                5000, 8000
        );

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            petalStormAbility.cast();
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 80, () -> {
            triggerKillTrapSequence(9);
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

                        });
                    }
            );
        });

        phaseFour = new BossAbilityPhase(warlordsNPC, 60, () -> {
            preventDashing = true;
            warlordsNPC.teleport(mapCenter.clone().add(0, 40, 0));
            warlordsNPC.setStunTicks(99999);
            warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, 99999);
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

                int t = 0;
                @Override
                public void run() {
                    t++;
                    // platforms - 1
                    if (conduitsOne.isCompleted() && !oneTriggered) {
                        triggerNextSequence(conduitsTwo);
                        oneTriggered = true;
                    } else if (conduitsOne.failed() && !oneTriggered) {
                        triggerNextSequence(conduitsTwo);
                        failedSequence();
                        oneTriggered = true;
                    }

                    // platforms - 2
                    if (conduitsTwo.isCompleted() && !twoTriggered) {
                        triggerNextSequence(conduitsThree);
                        twoTriggered = true;
                    } else if (conduitsTwo.failed() && !twoTriggered) {
                        triggerNextSequence(conduitsThree);
                        failedSequence();
                        twoTriggered = true;
                    }

                    // platforms - 3
                    if (conduitsThree.isCompleted() && !threeTriggered) {
                        triggerNextSequence(conduitsFour);
                        threeTriggered = true;
                    } else if (conduitsThree.failed() && !threeTriggered) {
                        triggerNextSequence(conduitsFour);
                        failedSequence();
                        threeTriggered = true;
                    }

                    // platforms - 4
                    if (conduitsFour.isCompleted() && !fourTriggered) {
                        triggerNextSequence(conduitsFive);
                        fourTriggered = true;
                    } else if (conduitsFour.failed() && !fourTriggered) {
                        triggerNextSequence(conduitsFive);
                        failedSequence();
                        fourTriggered = true;
                    }

                    // platforms - end
                    if (conduitsFive.isCompleted() && !fiveTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        fiveTriggered = true;
                    } else if (conduitsFive.failed() && !fiveTriggered) {
                        platformsController.triggerNextDrop(warlordsNPC.getGame());
                        failedSequence();
                        fiveTriggered = true;
                    }

                    if (t % 300 == 0 && t > 0) {
                        for (int i = 0; i < option.playerCount(); i++) {
                            option.spawnNewMob(new CrystallinePetal(warlordsNPC.getLocation()));
                        }
                    }

                    // teleport back to arena
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
                        warlordsNPC.getKnockback().removeModifier("KB RES");
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);

        });

        phaseFive = new BossAbilityPhase(warlordsNPC, 90, () -> {
            preventDashing = true;
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Pathetic, strong as a group... now DIVIDED.", TextColor.color(255, 150, 190)),
                    20, 60, 20
            );

            // fetch players in 3 groups
            int limit = 8 / 3;
            Game game = warlordsNPC.getGame();
            List<WarlordsEntity> arenaOnePlayers = PlayerFilter.playingGame(game)
                    .aliveEnemiesOf(warlordsNPC)
                    .limit(limit)
                    .stream().toList();
            List<WarlordsEntity> arenaTwoPlayers = PlayerFilter.playingGame(game)
                    .aliveEnemiesOf(warlordsNPC)
                    .filter(p -> !arenaOnePlayers.equals(p))
                    .limit(limit)
                    .stream().toList();
            List<WarlordsEntity> arenaThreePlayers = PlayerFilter.playingGame(game)
                    .aliveEnemiesOf(warlordsNPC)
                    .filter(p -> !arenaOnePlayers.equals(p))
                    .filter(p -> !arenaTwoPlayers.equals(p))
                    .limit(limit)
                    .stream().toList();

            // tp players
            arenaShift.teleportPlayersToArenaOne(arenaOnePlayers);
            arenaShift.teleportPlayersToArenaTwo(arenaTwoPlayers);
            arenaShift.teleportPlayersToArenaThree(arenaThreePlayers);

            // tp boss
            arenaShift.teleportBoss(warlordsNPC);

            new GameRunnable(game) {
                int t = 0;
                @Override
                public void run() {
                    t++;

                    if (t % 300 == 0) {
                        laserBarrageCenter.start(arenaOnePlayers);
                        laserBarrageCenter.start(arenaTwoPlayers);
                        laserBarrageCenter.start(arenaThreePlayers);
                    }
                }
            }.runTaskTimer(100, 0);
        });

        phaseSix = new BossAbilityPhase(warlordsNPC, 40, () -> {

        });

        phaseSeven = new BossAbilityPhase(warlordsNPC, 30, () -> {
            triggerKillTrapSequence(18);
        });

        phaseEight = new BossAbilityPhase(warlordsNPC, 20, () -> {
            // raining swords + 2 targets become heroes phase
            preventDashing = true;
            warlordsNPC.teleport(mapCenter.clone().add(0, 40, 0));
            warlordsNPC.addSpeedModifier(warlordsNPC, "Lilium Slowness", -99, 30 * 20);

            List<WarlordsEntity> protectors = PlayerFilter.playingGame(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
                    .limit(2).stream().toList();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < protectors.size(); i++) {
                if (i > 0) sb.append(" - ");
                sb.append(protectors.get(i).getName());
            }

            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.text("Chosen Champions:"),
                    Component.text(sb.toString(), NamedTextColor.DARK_AQUA),
                    20, 40, 20
            );
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.empty(),
                            Component.text("Beware, only 4 players at the time may receive protection from 1 champion.", NamedTextColor.DARK_AQUA),
                            20, 40, 20
                    );
                }
            }.runTaskLater(50);

            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;

                    if (t == 1) {
                        protectors.forEach(player -> {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0, false));
                            player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Overlord Raindown",
                                    "OVERLORD RAINDOWN",
                                    DamageCheck.class,
                                    DamageCheck.DAMAGE_CHECK,
                                    warlordsNPC,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    30 * 20,
                                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                        new CircleEffect(
                                                player.getGame(),
                                                player.getTeam(),
                                                player.getLocation().clone().add(0, 0.25, 0),
                                                4,
                                                new CircumferenceEffect(Particle.CHERRY_LEAVES, Particle.CHERRY_LEAVES).particlesPerCircumference(0.8),
                                                new DoubleLineEffect(Particle.CHERRY_LEAVES)
                                        ).playEffects();
                                        if (ticksLeft % 2 == 0) {
                                            for (WarlordsEntity ally : PlayerFilter
                                                    .entitiesAround(player, 4, 100, 4)
                                                    .aliveTeammatesOfExcludingSelf(player)
                                                    .limit(4)
                                            ) {
                                                ally.getCooldownManager().removeCooldown(PhysiraCheck.class, false);
                                                ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                        "Overlord Raindown",
                                                        "OVERLORD RAINDOWN",
                                                        PhysiraCheck.class,
                                                        PhysiraCheck.PHYSIRA_CHECK,
                                                        player,
                                                        CooldownTypes.ABILITY,
                                                        cooldownManager -> {
                                                        },
                                                        3
                                                ) {
                                                    @Override
                                                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                                        return currentDamageValue * 0;
                                                    }
                                                });
                                            }
                                        }
                                    })
                            ) {
                                @Override
                                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                    return currentDamageValue * 0;
                                }
                            });
                        });
                    }

                    if (t % 10 == 0) {
                        heavenlySpearAbility.start(warlordsNPC.getGame());
                        PlayerFilter.playingGame(warlordsNPC.getGame())
                                .filter(protectors::contains).forEach(protector -> PlayerFilter
                                        .entitiesAround(protector, 15, 100, 15)
                                        .aliveTeammatesOfExcludingSelf(protector)
                                        .forEach(otherProtector -> {
                                            protector.addInstance(InstanceBuilder
                                                    .damage()
                                                    .cause("Champion Disguise")
                                                    .source(warlordsNPC)
                                                    .value(1500)
                                                    .flags(InstanceFlags.TRUE_DAMAGE)
                                            );
                                            otherProtector.addInstance(InstanceBuilder
                                                    .damage()
                                                    .cause("Champion Disguise")
                                                    .source(warlordsNPC)
                                                    .value(1500)
                                                    .flags(InstanceFlags.TRUE_DAMAGE)
                                            );
                                        }));
                    }

                    if (t == 601) {
                        this.cancel();
                        warlordsNPC.setStunTicks(0);
                        warlordsNPC.teleport(option.getRandomSpawnLocation(warlordsNPC));
                        preventDashing = false;
                    }
                }

            }.runTaskTimer(100, 0);
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        EffectUtils.playCircularEffectAround(warlordsNPC.getGame(), warlordsNPC.getLocation(), Particle.CHERRY_LEAVES, 1, 1.3, 0.1, 1.7, 8, 1, 4, ticksElapsed);
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.CHERRY_LEAVES);
        }

        if (ticksElapsed % 500 == 0) {
            Random rand = new Random();
            roseGardenAbility.setRoseCount(rand.nextInt(10));
            roseGardenAbility.setRingRadius(rand.nextInt(12,24));
            roseGardenAbility.cast();
        }

        if (ticksElapsed % 320 == 0) {
            bouquetBarrageAbility.cast();
        }

        if (ticksElapsed % 600 == 0 && ticksElapsed > 0) {
            orbitalStrikeAbility.cast();
        }

        if (ticksElapsed % 480 == 0 && ticksElapsed > 0 && !preventDashing) {
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

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!engimas.isEmpty()) {
            event.setCancelled(true);
            attacker.sendMessage(Component.text("As long as my followers live, i will walk on this earth until the end of my days.", NamedTextColor.RED));
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        oribitingItemManager.stop();
        oribitingItemManagerFloating.stop();
        platformsController.stop();
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

    private void failedSequence() {
        Location loc = warlordsNPC.getLocation();
        Utils.playGlobalSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
        EffectUtils.strikeLightningInCylinder(loc, 10, false);
        PlayerFilter.entitiesAround(warlordsNPC, 28, 28, 28)
                .aliveEnemiesOf(warlordsNPC)
                .forEach(enemy -> {
                    enemy.addInstance(InstanceBuilder.damage()
                            .cause("Sequence Fail")
                            .source(warlordsNPC)
                            .min(3500)
                            .max(4000)
                            .flags(InstanceFlags.TRUE_DAMAGE)
                    );

                    new GameRunnable(warlordsNPC.getGame()) {
                        @Override
                        public void run() {
                            enemy.addInstance(InstanceBuilder.damage()
                                    .cause("Conduit Curse")
                                    .source(warlordsNPC)
                                    .min(200)
                                    .max(300)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                            if (enemy.getLocation().getY() < 45 || conduitsFive.failed() || conduitsFive.isCompleted()) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(20, 20);
                }
        );

        // heal boss
        warlordsNPC.addInstance(InstanceBuilder.healing()
                .cause("Sequence Fail")
                .source(warlordsNPC)
                .value(200000)
        );
    }

    private void triggerNextSequence(CrystalConduitsAbility ability) {
        platformsController.triggerNextDrop(warlordsNPC.getGame());
        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                ability.setFollowBoss(true);
                ability.start(warlordsNPC.getGame());
            }
        }.runTaskLater(30);
    }

    private void triggerKillTrapSequence(double amount) {
        new GameRunnable(warlordsNPC.getGame()) {
            int t = 0;
            @Override
            public void run() {
                Location enigmaLoc = mapCenter.clone().add(0, 1, 0);
                if (t++ < amount) {
                    double angle = t / amount * Math.PI * 2;
                    enigmaLoc.setX(mapCenter.getX() + Math.sin(angle) * 25);
                    enigmaLoc.setZ(mapCenter.getZ() + cos(angle) * 25);
                    LiliathEngima crystal = new LiliathEngima(enigmaLoc, PlayerFilter.playingGame(warlordsNPC.getGame())
                            .aliveEnemiesOf(warlordsNPC)
                            .stream().toList()
                    );

                    pveOption.spawnNewMob(crystal, Team.RED);
                    engimas.add(crystal.getWarlordsNPC().getUuid());

                    listener = new Listener() {
                        @EventHandler(ignoreCancelled = true)
                        private void onAllyDeath(WarlordsDeathEvent event) {
                            if (engimas.isEmpty()) return;

                            engimas.removeIf(p -> p.equals(event.getWarlordsEntity().getUuid()));
                            Utils.playGlobalSound(mapCenter, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 500, 2f);
                        }
                    };

                    warlordsNPC.getGame().registerEvents(listener);

                    Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                }

                if (t == 9) {
                    this.cancel();
                }
            }
        }.runTaskTimer(40, 6);
    }

    private void teleportToArena(List<WarlordsEntity> players) {

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
