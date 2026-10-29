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
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.*;

public class Lilium extends AbstractMob implements BossMob {

    private static final double ARENA_RETURN_Y = 45;
    private static final TextColor PETAL = TextColor.color(255, 105, 130);
    private static final TextColor SPEECH = TextColor.color(255, 150, 190);
    private static final TextColor HEART = TextColor.color(255, 90, 180);
    private static final Particle.DustOptions BOUQUET_DUST = new Particle.DustOptions(Color.fromRGB(255, 105, 180), 1.05f);

    private final List<BossAbilityPhase> phases = new ArrayList<>();
    private final List<GameRunnable> tasks = new ArrayList<>();
    private final List<UUID> enigmas = new ArrayList<>();
    private final List<UUID> crystals = new ArrayList<>();
    private final List<UUID> echoes = new ArrayList<>();
    private final Random random = new Random();

    private Listener minionDeathListener;
    private Location mapCenter;

    private OrbitingItemManager orbitingSwords;
    private OrbitingItemManager orbitingSkySwords;
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

    private List<WarlordsEntity> arenaOnePlayers = List.of();
    private List<WarlordsEntity> arenaTwoPlayers = List.of();
    private List<WarlordsEntity> arenaThreePlayers = List.of();

    private boolean preventDashing = false;
    private boolean skySequenceActive = false;
    private boolean bossReachedSky = false;
    private boolean arenaSplitActive = false;

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
                cooldownManager -> {
                },
                true
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS, (event, currentDamageValue, isCrit) -> {
            if (crystals.isEmpty()) {
                return;
            }
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.1f);
        }));

        playHeartSounds(warlordsNPC.getLocation());
        playHeartFirework(warlordsNPC.getLocation());

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);
        arenaShift = new ArenaShiftAbility(warlordsNPC.getWorld());
        createOrbitingSwords();
        createAbilities(option);
        createPhases();
        registerMinionDeathListener();
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getCooldownManager().removeCooldown(SoulShackle.class, false);
        playAura(ticksElapsed);
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.CHERRY_LEAVES);
        }

        if (ticksElapsed % 460 == 0) {
            roseGardenAbility.setRoseCount(Math.max(1, random.nextInt(10)));
            roseGardenAbility.setRingRadius(random.nextInt(12, 24));
            roseGardenAbility.cast();
        }

        if (ticksElapsed % 300 == 0) {
            bouquetBarrageAbility.cast();
        }

        if (ticksElapsed % 500 == 0 && ticksElapsed > 0) {
            orbitalStrikeAbility.cast();
        }

        if (ticksElapsed % 190 == 0) {
            echoOfCuts(2500, 3500);
        }

        if (ticksElapsed % 900 == 0 && ticksElapsed > 0 && !preventDashing) {
            spawnCrystallinePetals(option.playerCount(), option.getRandomSpawnLocation(warlordsNPC));
        }

        if (ticksElapsed % 290 == 0 && ticksElapsed > 0 && !preventDashing) {
            repeatWaltz(3, 0, 10, null);
        }

        float health = warlordsNPC.getCurrentHealth();
        for (BossAbilityPhase phase : phases) {
            phase.initialize(health);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!enigmas.isEmpty()) {
            event.setCancelled(true);
            attacker.sendMessage(Component.text("As long as my followers live, i will walk on this earth until the end of my days.", NamedTextColor.RED));
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        cancelTasks();
        if (orbitingSwords != null) {
            orbitingSwords.stop();
        }
        if (orbitingSkySwords != null) {
            orbitingSkySwords.stop();
        }
        if (platformsController != null) {
            platformsController.stop();
        }
        stopConduits();
        if (laserBarrageCenter != null) {
            laserBarrageCenter.cancel();
            laserBarrageCenter = null;
        }
        endArenaSplit(false);
        endSkySequence(false);
        unregisterMinionDeathListener();

        Location loc = warlordsNPC.getLocation();
        for (int i = 0; i < 10; i++) {
            EffectUtils.strikeLightning(loc, false);
        }
        Utils.playGlobalSound(loc, Sound.ENTITY_WITHER_DEATH, 500, 0.5f);
        playHeartFirework(loc);

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.text("The Heart Foundation will only get stronger... We will meet again!", HEART)
                );
                Utils.playGlobalSound(loc, Sound.ENTITY_WITHER_DEATH, 500, 0.5f);
            }
        }.runTaskLater(100);
    }

    private void createOrbitingSwords() {
        orbitingSwords = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 0.5, 0.5, 6, 0.5f, warlordsNPC, Material.STICK);
        orbitingSwords.spawnItems(6);
        orbitingSwords.start();
        orbitingSkySwords = new OrbitingItemManager(() -> warlordsNPC.getLocation().clone().add(0, 8, 0), 34, 3, 1, 15, warlordsNPC, Material.STICK);
    }

    private void createAbilities(PveOption option) {
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
        conduitsOne = createConduit(2, option);
        conduitsTwo = createConduit(3, option);
        conduitsThree = createConduit(3, option);
        conduitsFour = createConduit(4, option);
        conduitsFive = createConduit(5, option);
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
                64,
                1000, 1500,
                true,
                7,
                7000, 10000
        );
    }

    private CrystalConduitsAbility createConduit(int crystalCount, PveOption option) {
        return new CrystalConduitsAbility(
                warlordsNPC,
                () -> warlordsNPC.getLocation(),
                crystalCount,
                18,
                2,
                0.05,
                4000,
                option
        );
    }

    private void createPhases() {
        phases.add(new BossAbilityPhase(warlordsNPC, 90, () -> {
            crystalProtectionAbility(3);
            petalStormAbility.cast();
        }));
        phases.add(new BossAbilityPhase(warlordsNPC, 80, () -> triggerKillTrapSequence(9)));
        phases.add(new BossAbilityPhase(warlordsNPC, 70, this::startBladeWaltzPhase));
        phases.add(new BossAbilityPhase(warlordsNPC, 60, this::startSkyPhase));
        phases.add(new BossAbilityPhase(warlordsNPC, 50, () -> {
            crystalProtectionAbility(6);
            petalStormAbility.cast();
        }));
        phases.add(new BossAbilityPhase(warlordsNPC, 40, this::startArenaSplit));
        phases.add(new BossAbilityPhase(warlordsNPC, 30, () -> triggerKillTrapSequence(18)));
        phases.add(new BossAbilityPhase(warlordsNPC, 25, () -> crystalProtectionAbility(9)));
        phases.add(new BossAbilityPhase(warlordsNPC, 20, this::startChampionRain));
        phases.add(new BossAbilityPhase(warlordsNPC, 10, () -> ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("You are too late. The seal has already been weakened...", HEART)
        )));
    }

    private void startBladeWaltzPhase() {
        preventDashing = true;
        subtitle("This blade can inflict a thousand wounds if it has to!", SPEECH);
        repeatWaltz(20, 40, 6, () -> preventDashing = false);
    }

    private void startSkyPhase() {
        preventDashing = true;
        skySequenceActive = true;
        bossReachedSky = false;
        moveBoss(mapCenter.clone().add(0, 40, 0));
        warlordsNPC.getMob().removeTarget();
        warlordsNPC.setStunTicks(99999);
        warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, 99999);
        warlordsNPC.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 61, 60, false));
        for (WarlordsEntity enemy : aliveEnemies()) {
            enemy.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 80, false));
        }

        runLater(80, () -> {
            if (!skySequenceActive || warlordsNPC.isDead()) {
                return;
            }
            for (WarlordsEntity enemy : aliveEnemies()) {
                EffectUtils.strikeLightning(enemy.getLocation(), false);
            }
            warlordsNPC.getMob().removeTarget();
            conduitsOne.setFollowBoss(true);
            conduitsOne.setFollowLerp(1);
            conduitsOne.start(warlordsNPC.getGame());
            orbitingSkySwords.spawnItems(9);
            orbitingSkySwords.start();
            platformsController.start(warlordsNPC.getGame());
        });

        runTimer(0, 0, new GameRunnable(warlordsNPC.getGame()) {
            boolean oneTriggered = false;
            boolean twoTriggered = false;
            boolean threeTriggered = false;
            boolean fourTriggered = false;
            boolean fiveTriggered = false;
            int ticks = 0;

            @Override
            public void run() {
                if (!skySequenceActive) {
                    cancel();
                    return;
                }
                ticks++;
                oneTriggered = advanceConduit(oneTriggered, conduitsOne, conduitsTwo);
                twoTriggered = advanceConduit(twoTriggered, conduitsTwo, conduitsThree);
                threeTriggered = advanceConduit(threeTriggered, conduitsThree, conduitsFour);
                fourTriggered = advanceConduit(fourTriggered, conduitsFour, conduitsFive);
                if (!fiveTriggered && conduitsFive.isCompleted()) {
                    platformsController.triggerNextDrop(warlordsNPC.getGame());
                    fiveTriggered = true;
                } else if (!fiveTriggered && conduitsFive.failed()) {
                    platformsController.triggerNextDrop(warlordsNPC.getGame());
                    failedSequence();
                    fiveTriggered = true;
                }

                if (ticks % 300 == 0) {
                    spawnCrystallinePetals(pveOption.playerCount(), warlordsNPC.getLocation());
                }

                if (!bossReachedSky && warlordsNPC.getLocation().getY() >= ARENA_RETURN_Y) {
                    bossReachedSky = true;
                }
                boolean fellBack = bossReachedSky && warlordsNPC.getLocation().getY() < ARENA_RETURN_Y;
                boolean neverAscended = !bossReachedSky && ticks > 200;
                if (fellBack || neverAscended) {
                    endSkySequence(true);
                    cancel();
                }
            }
        });
    }

    private void startArenaSplit() {
        preventDashing = true;
        arenaSplitActive = true;
        echoes.clear();
        subtitle("Pathetic, strong as a group... now DIVIDED.", SPEECH);

        int minLimit = pveOption.playerCount() > 6 ? 3 : 2;
        int limit = Math.max(minLimit, Math.round(pveOption.playerCount() / 3f));
        Game game = warlordsNPC.getGame();
        arenaOnePlayers = alivePlayers(game).limit(limit).stream().toList();
        arenaTwoPlayers = alivePlayers(game)
                .filter(player -> !arenaOnePlayers.contains(player))
                .limit(limit)
                .stream().toList();
        arenaThreePlayers = alivePlayers(game)
                .filter(player -> !arenaOnePlayers.contains(player))
                .filter(player -> !arenaTwoPlayers.contains(player))
                .limit(limit)
                .stream().toList();

        arenaShift.teleportPlayersToArenaOne(arenaOnePlayers);
        arenaShift.teleportPlayersToArenaTwo(arenaTwoPlayers);
        arenaShift.teleportPlayersToArenaThree(arenaThreePlayers);
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

        runTimer(100, 0, new GameRunnable(game) {
            int ticks = 0;
            boolean spawned = false;

            @Override
            public void run() {
                if (!arenaSplitActive) {
                    cancel();
                    return;
                }
                ticks++;
                if (ticks % 360 == 0 && laserBarrageCenter != null) {
                    laserBarrageCenter.start(arenaOnePlayers);
                    laserBarrageCenter.start(arenaTwoPlayers);
                    laserBarrageCenter.start(arenaThreePlayers);
                }
                if (ticks == 1) {
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WITHER_SPAWN, 500, 0.5f);
                    spawnEcho(arenaOnePlayers, arenaShift.getArenaOne());
                    spawnEcho(arenaTwoPlayers, arenaShift.getArenaTwo());
                    spawnEcho(arenaThreePlayers, arenaShift.getArenaThree());
                    spawned = true;
                }
                if (spawned && echoes.isEmpty()) {
                    endArenaSplit(true);
                    cancel();
                }
            }
        });
    }

    private void startChampionRain() {
        preventDashing = true;
        moveBoss(mapCenter.clone().add(0, 40, 0));
        warlordsNPC.addSpeedModifier(warlordsNPC, "Lilium Slowness", -99, 30 * 20);

        List<WarlordsEntity> protectors = alivePlayers(warlordsNPC.getGame()).limit(2).stream().toList();
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < protectors.size(); i++) {
            if (i > 0) {
                names.append(" - ");
            }
            names.append(protectors.get(i).getName());
        }
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.text("My Chosen Champions:", SPEECH),
                Component.text(names.toString(), NamedTextColor.DARK_AQUA),
                20, 60, 20
        );
        protectors.forEach(protector -> protector.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40 * 20, 0, false)));

        Utils.playGlobalSound(mapCenter, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
        runLater(60, () -> {
            subtitle("Only 4 players at the time may receive protection from 1 champion.", NamedTextColor.DARK_AQUA);
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
        });

        runTimer(180, 0, new GameRunnable(warlordsNPC.getGame()) {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (ticks == 1) {
                    protectors.forEach(Lilium.this::grantChampionProtection);
                }
                if (ticks % 13 == 0) {
                    heavenlySpearAbility.start(warlordsNPC.getGame());
                }
                if (ticks == 601) {
                    cancel();
                    warlordsNPC.setStunTicks(0);
                    moveBoss(pveOption.getRandomSpawnLocation(warlordsNPC));
                    preventDashing = false;
                }
            }
        });
    }

    private void grantChampionProtection(WarlordsEntity protector) {
        protector.getCooldownManager().addCooldown(new RegularCooldown<>(
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
                            protector.getGame(),
                            protector.getTeam(),
                            protector.getLocation().clone().add(0, 0.25, 0),
                            6,
                            new CircumferenceEffect(Particle.CHERRY_LEAVES, Particle.CHERRY_LEAVES).particlesPerCircumference(0.8),
                            new DoubleLineEffect(Particle.CHERRY_LEAVES)
                    ).playEffects();
                    if (ticksLeft % 2 == 0) {
                        for (WarlordsEntity ally : PlayerFilter
                                .entitiesAround(protector, 6, 100, 6)
                                .aliveTeammatesOfExcludingSelf(protector)
                                .limit(4)
                        ) {
                            ally.getCooldownManager().removeCooldown(PhysiraCheck.class, false);
                            ally.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Overlord Raindown",
                                    "OVERLORD RAINDOWN",
                                    PhysiraCheck.class,
                                    PhysiraCheck.PHYSIRA_CHECK,
                                    protector,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    3
                            ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                                currentDamageValue.addModifier(FloatModifiable.ModifierType.OVERRIDING, name, 0);
                            }));
                        }
                    }
                })
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, 0.1f);
        }));
    }

    private void crystalProtectionAbility(double amount) {
        subtitle("Lilium's Legacy Petals have spawned, destroy them!", PETAL);
        runTimer(40, 10, new GameRunnable(warlordsNPC.getGame()) {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned++ < amount) {
                    double angle = spawned / amount * Math.PI * 2;
                    Location crystalLoc = mapCenter.clone().add(0, 1, 0);
                    crystalLoc.setX(mapCenter.getX() + Math.sin(angle) * 26);
                    crystalLoc.setZ(mapCenter.getZ() + Math.cos(angle) * 26);
                    PetalCrystal crystal = new PetalCrystal(crystalLoc);
                    pveOption.spawnNewMob(crystal, Team.RED);
                    crystals.add(crystal.getWarlordsNPC().getUuid());
                    Utils.playGlobalSound(crystal.getWarlordsNPC().getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 10, 0.5f);
                }
                if (crystals.isEmpty()) {
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_GHAST_HURT, 500, 0.5f);
                    petalsDestroyed();
                    cancel();
                }
            }
        });
    }

    private void triggerKillTrapSequence(double amount) {
        runTimer(40, 6, new GameRunnable(warlordsNPC.getGame()) {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned++ < amount) {
                    List<WarlordsEntity> aliveTargets = alivePlayers(warlordsNPC.getGame()).stream().toList();
                    if (!aliveTargets.isEmpty()) {
                        double angle = spawned / amount * Math.PI * 2;
                        Location enigmaLoc = mapCenter.clone().add(0, 1, 0);
                        enigmaLoc.setX(mapCenter.getX() + Math.sin(angle) * 25);
                        enigmaLoc.setZ(mapCenter.getZ() + Math.cos(angle) * 25);
                        LiliathEngima enigma = new LiliathEngima(enigmaLoc, aliveTargets);
                        pveOption.spawnNewMob(enigma, Team.RED);
                        if (enigma.getWarlordsNPC() != null) {
                            enigmas.add(enigma.getWarlordsNPC().getUuid());
                        }
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                    }
                }
                if (spawned >= amount) {
                    cancel();
                }
            }
        });
    }

    public static void bladeWaltzAbility(WarlordsNPC warlordsNPC) {
        Set<WarlordsEntity> hit = new HashSet<>();
        LocationBuilder location = new LocationBuilder(warlordsNPC.getEyeLocation());
        for (Block ignored : Utils.getTargetBlockInBetween(warlordsNPC.getEyeLocation(), 9)) {
            if (!Utils.getTargetBlock(location, 1).getType().isAir()
                    || !location.getBlock().getType().isAir()
                    || !location.clone().addY(1).getBlock().getType().isAir()
            ) {
                location.centerXZBlock();
                boolean isSlab = location.clone().addY(-1).getBlock().getBlockData() instanceof Slab;
                location.addY(isSlab ? -0.5 : 0);
                break;
            }
            PlayerFilter.entitiesAround(location.clone().addY(-1), 2, 2, 2)
                    .aliveEnemiesOf(warlordsNPC)
                    .excluding(hit)
                    .forEach(enemy -> {
                        hit.add(enemy);
                        enemy.addInstance(InstanceBuilder
                                .damage()
                                .cause("Waltz")
                                .source(warlordsNPC)
                                .min(2000)
                                .max(3000)
                        );
                    });
            location = location.forward(1);
            EffectUtils.displayParticle(Particle.CHERRY_LEAVES, location.clone().addY(-.5), 10, .1, .1, .1, 0);
        }
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ALLAY_DEATH, 500, 0.5f);
        moveNpc(warlordsNPC, location);
    }

    private void dropBouquetNode(Location at) {
        final Set<UUID> hitOnce = new HashSet<>();
        final double maxRadius = 3;
        runTimer(0, 1, new GameRunnable(warlordsNPC.getGame()) {
            double radius = 1;
            int life = 20;

            @Override
            public void run() {
                if (life-- <= 0 || radius > maxRadius) {
                    EffectUtils.displayParticle(Particle.CHERRY_LEAVES, at, 10, 0.3, 0.15, 0.3, 0.0);
                    EffectUtils.displayParticle(Particle.HEART, at, 18, 0.5, 0.25, 0.5, 0.05);
                    cancel();
                    return;
                }

                drawRingDust(at, radius, 24, BOUQUET_DUST);
                if (life % 6 == 0) {
                    at.getWorld().playSound(at, Sound.BLOCK_BEACON_ACTIVATE, 2, 0.5f);
                }
                PlayerFilter.entitiesAround(at, radius, 3, radius)
                        .aliveEnemiesOf(warlordsNPC)
                        .forEach(enemy -> {
                            if (!hitOnce.add(enemy.getUuid())) {
                                return;
                            }
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Waltz Bloom")
                                    .source(warlordsNPC)
                                    .min(1500)
                                    .max(2000)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                            Utils.addKnockback("Lilium Knockback", warlordsNPC.getLocation(), enemy, -1.15, 0.2);
                        });
                radius += 0.1;
            }
        });
    }

    private boolean advanceConduit(boolean alreadyTriggered, CrystalConduitsAbility current, CrystalConduitsAbility next) {
        if (alreadyTriggered) {
            return true;
        }
        if (current.isCompleted()) {
            triggerNextSequence(next);
            return true;
        }
        if (current.failed()) {
            triggerNextSequence(next);
            failedSequence();
            return true;
        }
        return false;
    }

    private void triggerNextSequence(CrystalConduitsAbility ability) {
        platformsController.triggerNextDrop(warlordsNPC.getGame());
        WarlordsEntity targetAtPlatform = PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .filter(player -> player.getLocation().getY() > ARENA_RETURN_Y)
                .excludingAlliedMobs()
                .findAnyOrNull();
        if (targetAtPlatform != null) {
            PlayerFilter.playingGame(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
                    .filter(player -> player.getLocation().getY() < ARENA_RETURN_Y)
                    .excludingAlliedMobs()
                    .forEach(player -> player.teleport(targetAtPlatform.getLocation()));
        }
        runLater(20, () -> {
            ability.setFollowBoss(true);
            ability.start(warlordsNPC.getGame());
        });
    }

    private void failedSequence() {
        Location loc = warlordsNPC.getLocation();
        Utils.playGlobalSound(loc, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
        EffectUtils.strikeLightningInCylinder(loc, 10, false);
        PlayerFilter.entitiesAround(warlordsNPC, 28, 28, 28)
                .aliveEnemiesOf(warlordsNPC)
                .forEach(enemy -> {
                    enemy.addInstance(InstanceBuilder
                            .damage()
                            .cause("Sequence Fail")
                            .source(warlordsNPC)
                            .min(4000)
                            .max(4500)
                            .flags(InstanceFlags.TRUE_DAMAGE)
                    );
                    runTimer(20, 20, new GameRunnable(warlordsNPC.getGame()) {
                        @Override
                        public void run() {
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Conduit Curse")
                                    .source(warlordsNPC)
                                    .min(250)
                                    .max(350)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                            if (enemy.getLocation().getY() < ARENA_RETURN_Y || conduitsFive.failed() || conduitsFive.isCompleted()) {
                                cancel();
                            }
                        }
                    });
                });
        warlordsNPC.addInstance(InstanceBuilder
                .healing()
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
                    player.addInstance(InstanceBuilder
                            .damage()
                            .cause("Petal Despair")
                            .source(warlordsNPC)
                            .min(2500)
                            .max(3500)
                            .flags(InstanceFlags.TRUE_DAMAGE)
                    );
                });
    }

    private void repeatWaltz(int repetitions, long delay, long period, Runnable onFinished) {
        runTimer(delay, period, new GameRunnable(warlordsNPC.getGame()) {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                bladeWaltzAbility(warlordsNPC);
                dropBouquetNode(warlordsNPC.getLocation());
                if (ticks == repetitions) {
                    cancel();
                    if (onFinished != null) {
                        onFinished.run();
                    }
                }
            }
        });
    }

    private void echoOfCuts(float min, float max) {
        playHeartFirework(warlordsNPC.getLocation());
        PlayerFilter.entitiesAround(warlordsNPC, 10, 10, 10)
                .aliveEnemiesOf(warlordsNPC)
                .forEach(player -> {
                    Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 5, 0.7f);
                    Utils.addKnockback(name, warlordsNPC.getLocation(), player, -1.5, 0.3);
                    player.addInstance(InstanceBuilder
                            .damage()
                            .cause("Echo of Cuts")
                            .source(warlordsNPC)
                            .min(min)
                            .max(max)
                            .flags(InstanceFlags.TRUE_DAMAGE));
                });
    }

    private void spawnEcho(List<WarlordsEntity> players, Location arena) {
        if (players.isEmpty()) {
            return;
        }
        AbstractMob echo = new EchoOfLilium(arena);
        pveOption.spawnNewMob(echo);
        if (echo.getWarlordsNPC() != null) {
            echoes.add(echo.getWarlordsNPC().getUuid());
        }
    }

    private void spawnCrystallinePetals(int count, Location location) {
        for (int i = 0; i < count; i++) {
            pveOption.spawnNewMob(new CrystallinePetal(location));
        }
    }

    private void endSkySequence(boolean returnBoss) {
        if (!skySequenceActive) {
            return;
        }
        skySequenceActive = false;
        if (orbitingSkySwords != null) {
            orbitingSkySwords.stop();
        }
        if (platformsController != null) {
            platformsController.stop();
        }
        if (returnBoss) {
            moveBoss(pveOption.getRandomSpawnLocation(warlordsNPC));
            warlordsNPC.setStunTicks(0);
            warlordsNPC.getKnockback().removeModifier("KB RES");
        }
        for (WarlordsEntity player : PlayerFilter
                .playingGame(warlordsNPC.getGame())
                .aliveEnemiesOf(warlordsNPC)
                .excludingAlliedMobs()
        ) {
            player.removePotionEffect(PotionEffectType.LEVITATION);
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            player.teleport(pveOption.getRandomSpawnLocation(player));
        }
        preventDashing = false;
    }

    private void endArenaSplit(boolean returnBoss) {
        if (!arenaSplitActive) {
            return;
        }
        arenaSplitActive = false;
        if (laserBarrageCenter != null) {
            laserBarrageCenter.cancel();
            laserBarrageCenter = null;
        }
        returnPlayers(arenaOnePlayers);
        returnPlayers(arenaTwoPlayers);
        returnPlayers(arenaThreePlayers);
        arenaOnePlayers = List.of();
        arenaTwoPlayers = List.of();
        arenaThreePlayers = List.of();
        echoes.clear();
        if (returnBoss) {
            moveBoss(pveOption.getRandomSpawnLocation(warlordsNPC));
        }
        preventDashing = false;
    }

    private void returnPlayers(List<WarlordsEntity> players) {
        for (WarlordsEntity player : players) {
            if (player.isDead()) {
                continue;
            }
            player.teleport(pveOption.getRandomSpawnLocation(player));
        }
    }

    private void stopConduits() {
        for (CrystalConduitsAbility conduit : new CrystalConduitsAbility[]{conduitsOne, conduitsTwo, conduitsThree, conduitsFour, conduitsFive}) {
            if (conduit != null) {
                conduit.stop();
            }
        }
    }

    private void registerMinionDeathListener() {
        minionDeathListener = new Listener() {
            @EventHandler(ignoreCancelled = true)
            public void onMinionDeath(WarlordsDeathEvent event) {
                UUID id = event.getWarlordsEntity().getUuid();
                if (mapCenter == null) {
                    return;
                }
                if (!enigmas.isEmpty()) {
                    enigmas.remove(id);
                    Utils.playGlobalSound(mapCenter, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3, 1f);
                }
                if (!crystals.isEmpty()) {
                    crystals.remove(id);
                }
                if (!echoes.isEmpty()) {
                    echoes.remove(id);
                    Utils.playGlobalSound(mapCenter, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 500, 0.5f);
                }
            }
        };
        warlordsNPC.getGame().registerEvents(minionDeathListener);
    }

    private void unregisterMinionDeathListener() {
        if (minionDeathListener == null) {
            return;
        }
        HandlerList.unregisterAll(minionDeathListener);
        minionDeathListener = null;
    }

    /**
     * Citizens keeps its own location and will pull an NPC back after a raw entity teleport.
     * Its 1.21 teleport is asynchronous, so record the destination there and also move the entity now.
     */
    private void moveBoss(Location location) {
        Location destination = location.clone();
        NPC citizensNpc = warlordsNPC.getNpc();
        if (citizensNpc != null && citizensNpc.isSpawned()) {
            citizensNpc.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
        warlordsNPC.teleport(destination);
    }

    private static void moveNpc(WarlordsNPC npc, Location location) {
        npc.teleportLocationOnly(location);
        Location destination = npc.getLocation().clone();
        destination.setX(location.getX());
        destination.setY(location.getY());
        destination.setZ(location.getZ());
        NPC citizensNpc = npc.getNpc();
        if (citizensNpc != null && citizensNpc.isSpawned()) {
            citizensNpc.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    private void playAura(int ticksElapsed) {
        double angle = ticksElapsed + 1 + Math.PI / 8.0;
        double y = Math.min(1.7, 0.1 * angle);
        Location particle = warlordsNPC.getLocation().clone().add(1.3 * Math.cos(angle), y, 1.3 * Math.sin(angle));
        EffectUtils.displayParticle(Particle.CHERRY_LEAVES, particle, 1);
    }

    private void playHeartSounds(Location location) {
        Utils.playGlobalSound(location, Sound.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS, 500, 0.5f);
        Utils.playGlobalSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
    }

    private void playHeartFirework(Location location) {
        EffectUtils.playFirework(location, FireworkEffect.builder()
                .withColor(Color.fromRGB(255, 90, 180))
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build()
        );
    }

    private void subtitle(String message, TextColor color) {
        ChatUtils.sendTitleToGamePlayers(
                warlordsNPC.getGame(),
                Component.empty(),
                Component.text(message, color),
                20, 60, 20
        );
    }

    private Iterable<WarlordsEntity> aliveEnemies() {
        return PlayerFilter.playingGame(warlordsNPC.getGame()).aliveEnemiesOf(warlordsNPC);
    }

    private PlayerFilter alivePlayers(Game game) {
        return PlayerFilter.playingGame(game)
                .aliveEnemiesOf(warlordsNPC)
                .excludingAlliedMobs();
    }

    private void runLater(long delay, Runnable action) {
        GameRunnable task = new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                action.run();
            }
        };
        beginTask(task).runTaskLater(delay);
    }

    private void runTimer(long delay, long period, GameRunnable task) {
        beginTask(task).runTaskTimer(delay, period);
    }

    private GameRunnable beginTask(GameRunnable task) {
        tasks.add(task);
        return task;
    }

    private void cancelTasks() {
        for (GameRunnable task : tasks) {
            try {
                task.cancel();
            } catch (IllegalStateException ignored) {
                // Task was never scheduled or already finished.
            }
        }
        tasks.clear();
    }

    private static void drawRingDust(Location center, double radius, int points, Particle.DustOptions dust) {
        final double step = (Math.PI * 2) / points;
        for (int i = 0; i < points; i++) {
            double angle = i * step;
            double x = center.getX() + Math.cos(angle) * radius;
            double z = center.getZ() + Math.sin(angle) * radius;
            Location point = new Location(center.getWorld(), x, center.getY(), z);
            EffectUtils.displayParticle(Particle.DUST, point, 1, dust);
        }
    }

}
