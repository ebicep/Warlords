package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.SoulShackle;
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
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.CrystallinePetal;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.EchoOfLilium;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.LiliathEngima;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.PetalCrystal;
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

import static java.lang.Math.cos;

public class Lilium extends AbstractMob implements BossMob {

    private Listener killSequenceListener;
    private Listener arenaSequenceListener;
    private Listener petalCrystalSequenceListener;
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
    private List<UUID> crystals = new ArrayList<>();

    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private BossAbilityPhase phaseFour;
    private BossAbilityPhase phaseFive;
    private BossAbilityPhase phaseSix;
    private BossAbilityPhase phaseSeven;
    private BossAbilityPhase phaseEight;
    private BossAbilityPhase phaseNine;
    private BossAbilityPhase phaseTen;

    private boolean finalPhase = false;

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
    public Mob getMobRegistry() {
        return Mob.LILIUM;
    }

    @Override
    public Component getDescription() {
        return Component.text("Queen of Hearts", TextColor.color(218, 112, 214));
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(255, 192, 203);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                null,
                warlordsNPC,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                true
        ).addModifier(Modifier.DAMAGE_AFTER_ALL_SELF, (event, currentDamageValue, isCrit) -> {
                    if (crystals.isEmpty()) {
                        return;
                    }
                    currentDamageValue.addMultiplicativeModifierMult(name, 0.1f);
                }
        ));

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS, 500, 0.5f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                          .withColor(Color.fromRGB(255, 90, 180))
                                                                          .with(FireworkEffect.Type.BALL_LARGE)
                                                                          .withTrail()
                                                                          .build()
        );

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);
        arenaShift = new ArenaShiftAbility(warlordsNPC.getWorld());

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
                30,
                2,
                Material.CHERRY_LEAVES,
                null,
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
                3000,
                4000,
                true,
                Material.CRIMSON_FUNGUS,
                8,
                true,
                1,
                4
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
                List.of(34.0, 34.0, 34.0, 34.0, 34.0),
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
                    WarlordsEntity target = PlayerFilter.entitiesAround(warlordsNPC, 50, 50, 50)
                                                        .aliveEnemiesOf(warlordsNPC)
                                                        .closestFirst(warlordsNPC)
                                                        .excludingAlliedMobs()
                                                        .findFirstOrNull();
                    return target != null ? target.getLocation().clone() : mapCenter.clone();
                },
                50,
                60,
                20,
                3,
                150,
                64, // maxTrace
                1000, 1500,
                true,
                7,
                7000, 10000
        );

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            crystalProtectionAbility(3);
            petalStormAbility.cast();
        }
        );

        phaseTwo = new BossAbilityPhase(warlordsNPC, 80, () -> {
            triggerKillTrapSequence(9);
        }
        );

        phaseThree = new BossAbilityPhase(warlordsNPC, 70, () -> {
            preventDashing = true;
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("This blade can inflict a thousand wounds if it has to!", TextColor.color(255, 150, 190)),
                    20, 60, 20
            );
            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;

                @Override
                public void run() {
                    t++;
                    bladeWaltsAbility(warlordsNPC);
                    dropBouquetNode(warlordsNPC.getLocation());
                    if (t == 20) {
                        this.cancel();
                        preventDashing = false;
                    }
                }
            }.runTaskTimer(40, 6);
        }
        );

        phaseFour = new BossAbilityPhase(warlordsNPC, 60, () -> {
            preventDashing = true;
            warlordsNPC.teleport(mapCenter.clone().add(0, 40, 0));
            warlordsNPC.getMob().removeTarget();
            warlordsNPC.setStunTicks(99999);
            warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, 99999);
            warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 61, 60, false));

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

                    warlordsNPC.getMob().removeTarget();
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
                                .excludingAlliedMobs()
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

        }
        );

        phaseFive = new BossAbilityPhase(warlordsNPC, 50, () -> {
            crystalProtectionAbility(6);
            petalStormAbility.cast();
        }
        );

        phaseSix = new BossAbilityPhase(warlordsNPC, 40, () -> {
            preventDashing = true;
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Pathetic, strong as a group... now DIVIDED.", TextColor.color(255, 150, 190)),
                    20, 60, 20
            );

            // fetch players in 3 groups
            int minLimit = option.playerCount() > 6 ? 3 : 2;
            int limit = Math.max(minLimit, Math.round(option.playerCount() / 3f));
            Game game = warlordsNPC.getGame();
            List<WarlordsEntity> arenaOnePlayers = PlayerFilter.playingGame(game)
                                                               .aliveEnemiesOf(warlordsNPC)
                                                               .excludingAlliedMobs()
                                                               .limit(limit)
                                                               .stream().toList();
            List<WarlordsEntity> arenaTwoPlayers = PlayerFilter.playingGame(game)
                                                               .aliveEnemiesOf(warlordsNPC)
                                                               .excludingAlliedMobs()
                                                               .filter(p -> !arenaOnePlayers.contains(p))
                                                               .limit(limit)
                                                               .stream().toList();
            List<WarlordsEntity> arenaThreePlayers = PlayerFilter.playingGame(game)
                                                                 .aliveEnemiesOf(warlordsNPC)
                                                                 .excludingAlliedMobs()
                                                                 .filter(p -> !arenaOnePlayers.contains(p))
                                                                 .filter(p -> !arenaTwoPlayers.contains(p))
                                                                 .limit(limit)
                                                                 .stream().toList();

            // tp players
            arenaShift.teleportPlayersToArenaOne(arenaOnePlayers);
            arenaShift.teleportPlayersToArenaTwo(arenaTwoPlayers);
            arenaShift.teleportPlayersToArenaThree(arenaThreePlayers);

            // tp boss
            arenaShift.teleportBoss(warlordsNPC);

            laserBarrageCenter = new LaserBarrageAbility(
                    warlordsNPC.getGame(),
                    warlordsNPC.getLocation(),
                    1,
                    60,
                    30,
                    30,
                    70,
                    2,
                    warlordsNPC
            );

            List<UUID> echoes = new ArrayList<>();
            new GameRunnable(game) {
                int t = 0;

                @Override
                public void run() {
                    t++;

                    if (t % 360 == 0) {
                        laserBarrageCenter.start(arenaOnePlayers);
                        laserBarrageCenter.start(arenaTwoPlayers);
                        laserBarrageCenter.start(arenaThreePlayers);
                    }

                    if (t == 1) {
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WITHER_SPAWN, 500, 0.5f);
                        arenaSequenceListener = new Listener() {
                            @EventHandler(ignoreCancelled = true)
                            private void onAllyDeath(WarlordsDeathEvent event) {
                                if (echoes.isEmpty()) {
                                    return;
                                }

                                echoes.removeIf(p -> p.equals(event.getWarlordsEntity().getUuid()));
                                Utils.playGlobalSound(mapCenter, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 500, 0.5f);
                            }
                        };
                        warlordsNPC.getGame().registerEvents(arenaSequenceListener);

                        if (!arenaOnePlayers.isEmpty()) {
                            AbstractMob echo = new EchoOfLilium(arenaShift.getArenaOne());
                            option.spawnNewMob(echo);
                            echoes.add(echo.getWarlordsNPC().getUuid());
                        }
                        if (!arenaTwoPlayers.isEmpty()) {
                            AbstractMob echo = new EchoOfLilium(arenaShift.getArenaTwo());
                            option.spawnNewMob(echo);
                            echoes.add(echo.getWarlordsNPC().getUuid());
                        }
                        if (!arenaThreePlayers.isEmpty()) {
                            AbstractMob echo = new EchoOfLilium(arenaShift.getArenaThree());
                            option.spawnNewMob(echo);
                            echoes.add(echo.getWarlordsNPC().getUuid());
                        }
                    }

                    if (echoes.isEmpty()) {
                        arenaOnePlayers.forEach(p -> p.teleport(option.getRandomSpawnLocation(p)));
                        arenaTwoPlayers.forEach(p -> p.teleport(option.getRandomSpawnLocation(p)));
                        arenaThreePlayers.forEach(p -> p.teleport(option.getRandomSpawnLocation(p)));
                        warlordsNPC.teleport(option.getRandomSpawnLocation(warlordsNPC));
                        this.cancel();
                    }
                }
            }.runTaskTimer(100, 0);
        }
        );

        phaseSeven = new BossAbilityPhase(warlordsNPC, 30, () -> {
            triggerKillTrapSequence(18);
        }
        );

        phaseEight = new BossAbilityPhase(warlordsNPC, 25, () -> {
            crystalProtectionAbility(9);
        }
        );

        phaseNine = new BossAbilityPhase(warlordsNPC, 20, () -> {
            // raining swords + 2 targets become heroes phase
            preventDashing = true;
            warlordsNPC.teleport(mapCenter.clone().add(0, 40, 0));
            warlordsNPC.addSpeedModifier(warlordsNPC, "Lilium Slowness", -99, 30 * 20);

            List<WarlordsEntity> protectors = PlayerFilter.playingGame(warlordsNPC.getGame())
                                                          .aliveEnemiesOf(warlordsNPC)
                                                          .excludingAlliedMobs()
                                                          .limit(2).stream().toList();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < protectors.size(); i++) {
                if (i > 0) {
                    sb.append(" - ");
                }
                sb.append(protectors.get(i).getName());
            }

            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.text("My Chosen Champions:", TextColor.color(255, 150, 190)),
                    Component.text(sb.toString(), NamedTextColor.DARK_AQUA),
                    20, 60, 20
            );

            protectors.forEach(
                    protector -> protector.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40 * 20, 0, false))
            );

            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.empty(),
                            Component.text("Only 4 players at the time may receive protection from 1 champion.", NamedTextColor.DARK_AQUA),
                            20, 60, 20
                    );
                    Utils.playGlobalSound(mapCenter, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
                }
            }.runTaskLater(60);

            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;

                @Override
                public void run() {
                    t++;

                    if (t == 1) {
                        protectors.forEach(player -> {
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
                                                6,
                                                new CircumferenceEffect(Particle.CHERRY_LEAVES, Particle.CHERRY_LEAVES).particlesPerCircumference(0.8),
                                                new DoubleLineEffect(Particle.CHERRY_LEAVES)
                                        ).playEffects();
                                        if (ticksLeft % 2 == 0) {
                                            for (WarlordsEntity ally : PlayerFilter
                                                    .entitiesAround(player, 6, 100, 6)
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
                                                ).addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                                                            currentDamageValue.addOverridingModifier(name, 0);
                                                        }
                                                ));
                                            }
                                        }
                                    })
                            ).addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                                        currentDamageValue.addMultiplicativeModifierMult(name, 0.1f);
                                    }
                            ));
                        });
                    }

                    if (t % 13 == 0) {
                        heavenlySpearAbility.start(warlordsNPC.getGame());
                    }

                    if (t == 601) {
                        this.cancel();
                        warlordsNPC.setStunTicks(0);
                        warlordsNPC.teleport(option.getRandomSpawnLocation(warlordsNPC));
                        preventDashing = false;
                    }
                }

            }.runTaskTimer(180, 0);
        }
        );

        phaseTen = new BossAbilityPhase(warlordsNPC, 10, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.text("You are too late. The seal has already been weakened...", TextColor.color(255, 90, 180))
            );
//            finalPhase = true;
//            ChatUtils.sendTitleToGamePlayers(
//                    warlordsNPC.getGame(),
//                    Component.text("You are too late. The seal has already been weakened...", TextColor.color(255, 90, 180))
//            );
//            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 10, 0.5f);
//            // tp to arena
//            Location finalArenaLoc = new Location(warlordsNPC.getWorld(), -266.5, 29, 120.5);
//            option.getGame().warlordsPlayers().forEach(p -> p.teleport(finalArenaLoc));
//            warlordsNPC.teleport(finalArenaLoc);
//
//            // heal to 25%
//            warlordsNPC.addInstance(InstanceBuilder.healing()
//                    .cause("Final Phase")
//                    .source(warlordsNPC)
//                    .value(warlordsNPC.getMaxHealth() * 0.25f)
//            );
//            // buff
//            warlordsNPC.getSpeed().addBaseModifier(30);
//            warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
//                    "Enraged",
//                    null,
//                    Lilium.class,
//                    null,
//                    warlordsNPC,
//                    CooldownTypes.BUFF,
//                    cooldownManager -> {},
//                    true
//            ) {
//                @Override
//                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
//                    return currentDamageValue * 0.9f;
//                }
//
//                @Override
//                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
//                    return currentDamageValue * 1.5f;
//                }
//            });
        }
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getCooldownManager().removeCooldown(SoulShackle.class, false);

        EffectUtils.playCircularEffectAround(warlordsNPC.getGame(), warlordsNPC.getLocation(), Particle.CHERRY_LEAVES, 1, 1.3, 0.1, 1.7, 8, 1, 4, ticksElapsed);
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.CHERRY_LEAVES);
        }

        if (ticksElapsed % 460 == 0) {
            Random rand = new Random();
            roseGardenAbility.setRoseCount(rand.nextInt(10));
            roseGardenAbility.setRingRadius(rand.nextInt(12, 24));
            roseGardenAbility.cast();
        }

        if (ticksElapsed % 300 == 0) {
            bouquetBarrageAbility.cast();
        }

        if (ticksElapsed % 500 == 0 && ticksElapsed > 0) {
            orbitalStrikeAbility.cast();
        }

        if (ticksElapsed % 190 == 0) {
            EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                              .withColor(Color.fromRGB(255, 90, 180))
                                                                              .with(FireworkEffect.Type.BALL_LARGE)
                                                                              .withTrail()
                                                                              .build()
            );
            PlayerFilter.entitiesAround(warlordsNPC, 10, 10, 10)
                        .aliveEnemiesOf(warlordsNPC)
                        .forEach(player -> {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 5, 0.7f);
                            Utils.addKnockback(name, warlordsNPC.getLocation(), player, -1.5, 0.3);
                            player.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Echo of Cuts")
                                    .source(warlordsNPC)
                                    .min(2500)
                                    .max(3500)
                                    .flags(InstanceFlags.TRUE_DAMAGE));
                        });
        }

        if (ticksElapsed % 900 == 0 && ticksElapsed > 0 && !preventDashing) {
            for (int i = 0; i < option.playerCount(); i++) {
                option.spawnNewMob(new CrystallinePetal(option.getRandomSpawnLocation(warlordsNPC)));
            }
        }

        if (ticksElapsed % 290 == 0 && ticksElapsed > 0 && !preventDashing) {
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
        phaseNine.initialize(health);
        phaseTen.initialize(health);
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

        Location loc = warlordsNPC.getLocation();
        for (int i = 0; i < 10; i++) {
            EffectUtils.strikeLightning(loc, false);
        }
        Utils.playGlobalSound(loc, Sound.ENTITY_WITHER_DEATH, 500, 0.5f);
        EffectUtils.playFirework(loc, FireworkEffect.builder()
                                                    .withColor(Color.fromRGB(255, 90, 180))
                                                    .with(FireworkEffect.Type.BALL_LARGE)
                                                    .withTrail()
                                                    .build()
        );

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.text("The Heart Foundation will only get stronger... We will meet again!", TextColor.color(255, 90, 180))
                );
                Utils.playGlobalSound(loc, Sound.ENTITY_WITHER_DEATH, 500, 0.5f);
            }
        }.runTaskLater(100);
    }

    private void crystalProtectionAbility(double amount) {
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.empty(),
                Component.text("Lilium's Legacy Petals have spawned, destroy them!", TextColor.color(255, 105, 130)),
                20, 60, 20
        );
        new GameRunnable(warlordsNPC.getGame()) {
            int t = 0;

            @Override
            public void run() {
                Location crystalLoc = mapCenter.clone().add(0, 1, 0);
                if (t++ < amount) {
                    double angle = t / amount * Math.PI * 2;
                    crystalLoc.setX(mapCenter.getX() + Math.sin(angle) * 26);
                    crystalLoc.setZ(mapCenter.getZ() + cos(angle) * 26);
                    PetalCrystal crystal = new PetalCrystal(crystalLoc);

                    pveOption.spawnNewMob(crystal, Team.RED);
                    crystals.add(crystal.getWarlordsNPC().getUuid());

                    petalCrystalSequenceListener = new Listener() {
                        @EventHandler(ignoreCancelled = true)
                        private void onAllyDeath(WarlordsDeathEvent event) {
                            if (crystals.isEmpty()) {
                                return;
                            }

                            crystals.removeIf(p -> p.equals(event.getWarlordsEntity().getUuid()));
                        }
                    };
                    warlordsNPC.getGame().registerEvents(petalCrystalSequenceListener);

                    Utils.playGlobalSound(crystal.getWarlordsNPC().getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 10, 0.5f);
                }

                if (crystals.isEmpty()) {
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_GHAST_HURT, 500, 0.5f);
                    petalsDestroyed();
                    this.cancel();
                }
            }
        }.runTaskTimer(40, 10);
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
                                                                                     .excludingAlliedMobs()
                                                                                     .stream().toList()
                    );

                    pveOption.spawnNewMob(crystal, Team.RED);
                    engimas.add(crystal.getWarlordsNPC().getUuid());

                    killSequenceListener = new Listener() {
                        @EventHandler(ignoreCancelled = true)
                        private void onAllyDeath(WarlordsDeathEvent event) {
                            if (engimas.isEmpty()) {
                                return;
                            }

                            engimas.removeIf(p -> p.equals(event.getWarlordsEntity().getUuid()));
                            Utils.playGlobalSound(mapCenter, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3, 1f);
                        }
                    };

                    warlordsNPC.getGame().registerEvents(killSequenceListener);

                    Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                }

                if (t == amount) {
                    this.cancel();
                }
            }
        }.runTaskTimer(40, 6);
    }

    public static void bladeWaltsAbility(WarlordsNPC warlordsNPC) {
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
                                    .min(2000)
                                    .max(3000)
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
                                if (!hitOnce.add(id)) {
                                    return; // already hit by this bouquet
                                }
                                wp.addInstance(InstanceBuilder
                                        .damage()
                                        .cause("Waltz Bloom")
                                        .source(warlordsNPC)
                                        .min(1500)
                                        .max(2000)
                                        .flags(InstanceFlags.TRUE_DAMAGE)
                                );
                                Utils.addKnockback("Lilium Knockback", warlordsNPC.getLocation(), wp, -1.15, 0.2);
                            });

                r += 0.1;
            }
        }.runTaskTimer(0, 1);
    }

    private void triggerNextSequence(CrystalConduitsAbility ability) {
        platformsController.triggerNextDrop(warlordsNPC.getGame());

        // check for players that fell off
        WarlordsEntity targetAtPlatform = PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .filter(p -> p.getLocation().getY() > 45)
                .excludingAlliedMobs()
                .findAnyOrNull();

        if (targetAtPlatform != null) {
            PlayerFilter.playingGame(warlordsNPC.getGame())
                        .aliveEnemiesOf(warlordsNPC)
                        .filter(p -> p.getLocation().getY() < 45)
                        .excludingAlliedMobs()
                        .forEach(p -> {
                            p.teleport(targetAtPlatform.getLocation());
                        });
        }

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                ability.setFollowBoss(true);
                ability.start(warlordsNPC.getGame());
            }
        }.runTaskLater(20);
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
                                                                 .min(4000)
                                                                 .max(4500)
                                                                 .flags(InstanceFlags.TRUE_DAMAGE)
                                );

                                new GameRunnable(warlordsNPC.getGame()) {
                                    @Override
                                    public void run() {
                                        enemy.addInstance(InstanceBuilder.damage()
                                                                         .cause("Conduit Curse")
                                                                         .source(warlordsNPC)
                                                                         .min(250)
                                                                         .max(350)
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

    private void petalsDestroyed() {
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.empty(),
                Component.text("YOU MONSTERS!", TextColor.color(255, 0, 0)),
                20, 40, 20
        );
        PlayerFilter.entitiesAround(warlordsNPC, 28, 28, 28)
                    .aliveEnemiesOf(warlordsNPC)
                    .forEach(player -> {
                        EffectUtils.strikeLightning(player.getLocation(), false);
                        player.addInstance(InstanceBuilder.damage()
                                                          .cause("Petal Despair")
                                                          .source(warlordsNPC)
                                                          .min(2500)
                                                          .max(3500)
                                                          .flags(InstanceFlags.TRUE_DAMAGE)
                        );
                    });
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

}
