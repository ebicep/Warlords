package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.BossAbilityPhase;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.*;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.EchoOfBlades;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NineCrystal;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SoulReaver;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.RiftWalker;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class OneOfNine extends AbstractMob implements BossMob {

    private Listener listener;
    List<UUID> pylons = new ArrayList<>();
    private Location mapCenter;
    private Location mapLeft;
    private Location mapRight;
    private OrbitingItemManager swordManager;
    private OrbitingItemManager centerSwordManager;
    private DamagePhaseController damageController;
    private LaserBarrageAbility laserBarrageCenter;
    private LaserBarrageAbility laserBarrageLeft;
    private LaserBarrageAbility laserBarrageRight;
    private MeteorMarkersAbility meteorMarkersAbility;
    private ArenaCollapseAbility arenaCollapseAbility;
    private SpinningWallAbility spinningWallAbility;
    private GiantLaserAbility giantLaserAbility;
    private ChasingOrbsAbility chasingOrbsAbility;
    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private BossAbilityPhase phaseFour;
    private BossAbilityPhase phaseFive;

    private boolean preventMinions = false;
    private boolean preventDamagePhase = false;

    public OneOfNine(Location spawnLocation) {
        super(
                spawnLocation,
                "One of Nine",
                200000,
                0.2f,
                40,
                3000,
                5000
        );
    }

    public OneOfNine(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
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

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5);
        mapLeft = new Location(warlordsNPC.getWorld(), 87.5, 24, 87.5);
        mapRight = new Location(warlordsNPC.getWorld(), 137.5, 24, 37.5);

        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.GRAY)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.strikeLightningInCylinder(warlordsNPC.getLocation(), 10, false);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_6, 10, 0.5f);

        swordManager = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 6, 2, 3, 4, option, warlordsNPC, Material.NETHERITE_SWORD);
        centerSwordManager = new OrbitingItemManager(() -> mapCenter, 25, 30, 1, 30, option, warlordsNPC, Material.NETHERITE_SWORD);

        damageController = new DamagePhaseController(warlordsNPC);

        laserBarrageCenter = new LaserBarrageAbility(warlordsNPC.getGame(), mapCenter, option.playerCount(), 40, 15, 15, 70, 2, warlordsNPC);
        laserBarrageLeft = new LaserBarrageAbility(warlordsNPC.getGame(), mapLeft, option.playerCount(), 40, 15, 15, 70, 2, warlordsNPC);
        laserBarrageRight = new LaserBarrageAbility(warlordsNPC.getGame(), mapRight, option.playerCount(), 40, 15, 15, 70, 2, warlordsNPC);

        arenaCollapseAbility = new ArenaCollapseAbility(
                warlordsNPC,
                warlordsNPC,
                () -> mapCenter,
                32,
                16,
                1,
                100,
                400,
                1,
                25,
                1
        );

        meteorMarkersAbility = new MeteorMarkersAbility(
                warlordsNPC,
                warlordsNPC,
                () -> mapCenter,
                32,
                2 * option.playerCount(),
                40,
                6,
                3000,
                0.5,
                false,
                1.5,
                4,
                0,
                0
        );

        spinningWallAbility = new SpinningWallAbility(
                warlordsNPC,
                warlordsNPC,
                () -> mapCenter,
                38,
                1,
                1000,
                1,
                3,
                true,
                2000,
                10,
                Color.GRAY
        );

        giantLaserAbility = new GiantLaserAbility(
                warlordsNPC,
                warlordsNPC,
                () -> warlordsNPC.getEyeLocation(),
                50,
                15,
                70,
                1.3,
                2,
                1000,
                false,
                2
        );

        chasingOrbsAbility = new ChasingOrbsAbility(
                warlordsNPC,
                option.playerCount(),
                100,
                0.32,
                4,
                5000,
                1.5,
                Material.ENDER_EYE,
                2f,
                false,
                mapCenter
        );

        swordManager.spawnSwords(9);
        swordManager.start();

        centerSwordManager.spawnSwords(9);
        centerSwordManager.start();

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                null,
                warlordsNPC,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                true
        ) {
            @Override
            public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (damageController.isInDamageWindow()) {
                    return currentDamageValue;
                }
                // nullify reflected damage
                if (event.getFlags().contains(InstanceFlags.REFLECTIVE_DAMAGE)) {
                    return currentDamageValue * 0f;
                }

                event.getSource().addInstance(InstanceBuilder
                        .damage()
                        .source(warlordsNPC)
                        .cause("Greed")
                        .value(currentDamageValue * (event.getFlags().contains(InstanceFlags.DOT) ? 0.25f : 1))
                        .flags(InstanceFlags.RECURSIVE, InstanceFlags.IGNORE_DAMAGE_BOOST)
                );
                event.getSource().sendMessage(Component.text("Your divine punishment awaits if you keep giving in to your greed...", NamedTextColor.RED));
                event.setCancelled(true);
                return currentDamageValue;
            }
        });

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(warlordsNPC, 7, 7, 7)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    if (damageController.isInDamageWindow()) {
                        return;
                    }
                    we.addInstance(InstanceBuilder
                            .damage()
                            .min(1000)
                            .max(1500)
                            .source(warlordsNPC)
                            .cause("Reaving Blades")
                            .flags(InstanceFlags.TRUE_DAMAGE, InstanceFlags.PIERCE)
                    );
                }
                if (warlordsNPC.isDead()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 10);

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            preventDamagePhase = true;
            preventMinions = true;
            damageController.closeWindow();
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("You feel the gaze of dark energy surrounding you...", NamedTextColor.RED),
                    20,
                    60,
                    20
            );
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.5f);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    counter++;

                    if (counter % 120 == 0) {
                        laserBarrageCenter.start(option.getGame().warlordsPlayers().toList());
                    }

                    if (counter % 240 == 0) {
                        for (int i = 0; i < option.playerCount(); i++) {
                            option.spawnNewMob(new RiftWalker(option.getRandomSpawnLocation(warlordsNPC)));
                        }
                    }

                    if (counter == 801) {
                        preventDamagePhase = false;
                        preventMinions = false;
                        laserBarrageCenter.cancel();
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);

            phaseTransition();
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 70, () -> {
            preventDamagePhase = true;
            preventMinions = true;
            damageController.closeWindow();
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Destroy the nine ornaments before the timer runs out!", NamedTextColor.GOLD),
                    20,
                    60,
                    20
            );

            Location crystalLoc = mapCenter.clone();
            for (int j = 0; j < 9; j++) {
                double angle = j / 9D * Math.PI * 2;
                crystalLoc.setX(mapCenter.getX() + Math.sin(angle) * 20);
                crystalLoc.setZ(mapCenter.getZ() + cos(angle) * 20);
                NineCrystal crystal = new NineCrystal(crystalLoc, SpecType.VALUES[j % 3]);
                pveOption.spawnNewMob(crystal, Team.RED);
                pylons.add(crystal.getWarlordsNPC().getUuid());
            }

            listener = new Listener() {
                @EventHandler(ignoreCancelled = true)
                private void onAllyDeath(WarlordsDeathEvent event) {
                    if (pylons.isEmpty()) return;

                    pylons.removeIf(p -> p.equals(event.getWarlordsEntity().getUuid()));
                    Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 1.5f);
                }
            };

            warlordsNPC.getGame().registerEvents(listener);

            AtomicInteger countdown = new AtomicInteger(30);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                double angle = 0;
                @Override
                public void run() {
                    if (counter % 20 == 0) {
                        countdown.getAndDecrement();
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 500, 0.4f);
                    }

                    // floor death ray
                    angle += Math.toRadians(4);
                    Vector dir = new Vector(Math.cos(angle), 0, Math.sin(angle));
                    castDeathRay(mapCenter.clone().add(0, -2, 0), dir, 35, warlordsNPC);


                    if (pylons.isEmpty() && countdown.get() > 0) {
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.WHITE)
                                        .withTrail()
                                        .build()
                        );

                        preventDamagePhase = false;
                        preventMinions = false;
                        this.cancel();
                    }

                    if (countdown.get() <= 0) {
                        EffectUtils.strikeLightningTicks(warlordsNPC.getLocation(), true, 60);
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.RED)
                                        .withTrail()
                                        .build()
                        );

                        for (WarlordsEntity we : PlayerFilter
                                .playingGame(warlordsNPC.getGame())
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            we.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Valerian Death")
                                    .source(warlordsNPC)
                                    .min(700 * 100)
                                    .max(1300 * 100)
                                    .critChance(100)
                                    .critMultiplier(300)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                            EffectUtils.strikeLightning(we.getLocation(), false);
                            EffectUtils.playParticleLinkAnimation(
                                    we.getLocation(),
                                    mapCenter,
                                    Particle.CHERRY_LEAVES
                            );
                        }

                        preventDamagePhase = false;
                        preventMinions = false;
                        this.cancel();
                    }

                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.text(countdown.get(), NamedTextColor.GOLD),
                            Component.empty(),
                            0, 4, 0
                    );

                    counter++;
                }
            }.runTaskTimer(60, 0);

            phaseTransition();
        });

        phaseThree = new BossAbilityPhase(warlordsNPC, 50, () -> {
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.3f);
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Spinning Walls.. OF DEATH", NamedTextColor.RED),
                    20,
                    60,
                    20
            );

            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override
                public void run() {
                    t++;

                    if (t == 100) {
                        spinningWallAbility.start(warlordsNPC.getGame());
                    }

                    if (t % 100 == 0) {
                        meteorMarkersAbility.start(warlordsNPC.getGame());
                    }

                    if (t % 1001 == 0) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
        });

        phaseFour = new BossAbilityPhase(warlordsNPC, 30, () -> {
            preventMinions = true;
            preventDamagePhase = true;
            damageController.closeWindow();
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("You feel the gaze of dark energy surrounding you once more...", NamedTextColor.RED),
                    20,
                    60,
                    20
            );
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.4f);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    counter++;

                    if (counter % 100 == 0) {
                        List<WarlordsPlayer> playerList = option.getGame().warlordsPlayers().toList();
                        laserBarrageCenter.start(playerList);
                        laserBarrageLeft.start(playerList);
                        laserBarrageRight.start(playerList);
                    }

                    if (counter % 300 == 0) {
                        for (int i = 0; i < option.playerCount(); i++) {
                            option.spawnNewMob(new RiftWalker(option.getRandomSpawnLocation(warlordsNPC)));
                        }
                    }

                    if (counter == 801) {
                        preventMinions = false;
                        preventDamagePhase = false;
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
            phaseTransition();
        });

        phaseFive = new BossAbilityPhase(warlordsNPC, 15, () -> {
            preventMinions = true;
            arenaCollapseAbility.start(warlordsNPC.getGame());
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.3f);
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Did you really think this was the end?", NamedTextColor.GRAY),
                    20,
                    60,
                    20
            );

            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    counter++;

                    if (counter % 260 == 0) {
                        laserBarrageCenter.start(option.getGame().warlordsPlayers().toList());
                    }

                    if (warlordsNPC.isDead()) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(20, 0);
            Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.2f);
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WARDEN_ROAR, 500, 0.2f);
            warlordsNPC.getSpeed().addBaseModifier(60);
            warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Enraged",
                    null,
                    OneOfNine.class,
                    null,
                    warlordsNPC,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    true
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 0.7f;
                }

                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 1.5f;
                }
            });

            phaseTransition();
        });
    }

    public void castDeathRay(Location start, Vector direction, double length, WarlordsEntity caster) {
        direction.normalize();
        for (double i = 0; i < length; i += 0.5) {
            Location point = start.clone().add(direction.clone().multiply(i));
            EffectUtils.displayParticle(Particle.END_ROD, point, 5, 0, 0, 0, 0);

            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(point, 1.3, 1, 1.3)
                    .aliveEnemiesOf(caster)
            ) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Death Ray")
                        .value(700)
                        .source(warlordsNPC)
                );
            }
        }
    }

    public void phaseTransition() {
        FallingBlockWaveEffect.create(
                mapCenter.clone().add(0, 1, 0),
                20,
                7,
                Material.SOUL_FIRE
        );

        Utils.playGlobalSound(mapCenter, Sound.ENTITY_WITHER_DEATH, 0.5f, 0.2f);
        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.STAR)
                .withTrail()
                .build());
        EffectUtils.strikeLightningInCylinder(warlordsNPC.getLocation(), 10, false);

        for (WarlordsEntity we : PlayerFilter
                .entitiesAround(warlordsNPC, 20, 20, 20)
                .aliveEnemiesOf(warlordsNPC)
        ) {
            Utils.addKnockback("One of Nine", warlordsNPC.getLocation(), we, -20, 0.15);
            we.addInstance(InstanceBuilder
                    .damage()
                    .cause("Command of Nine")
                    .source(warlordsNPC)
                    .min(2000)
                    .max(3000)
            );
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getCooldownManager().removeCooldown(SoulShackle.class, false);
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.END_ROD);
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation().clone().add(0, 1.5, 0),
                    3,
                    new CircumferenceEffect(Particle.PORTAL, Particle.PORTAL).particlesPerCircumference(1.5)
            ).playEffects();
        }

        if (ticksElapsed % 1800 == 0 && ticksElapsed > 0 && !preventMinions) {
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 1.5f);
            for (int i = 0; i < option.playerCount(); i++) {
                option.spawnNewMob(new EchoOfBlades(pveOption.getRandomSpawnLocation(warlordsNPC)));
            }
        }

        if (ticksElapsed % 950 == 0 && ticksElapsed > 0 && !preventMinions) {
            Utils.playGlobalSound(mapCenter, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 500, 0.3f);
            for (int i = 0; i < option.playerCount(); i++) {
                option.spawnNewMob(new SoulReaver(pveOption.getRandomSpawnLocation(warlordsNPC)));
            }
        }

        if (ticksElapsed % 280 == 0 && ticksElapsed > 0 && !preventMinions) {
            giantLaserAbility.start(warlordsNPC.getGame());
        }

        if (ticksElapsed % 535 == 0 && ticksElapsed > 0 && !preventMinions) {
            chasingOrbsAbility.start(warlordsNPC.getGame());
        }

        if (ticksElapsed % 400 == 0 && ticksElapsed > 0 && !preventDamagePhase) {
            damageController.openWindow(10 * 20);
            PlayerFilter.playingGame(warlordsNPC.getGame())
                    .warlordPlayersFirst()
                    .forEach(we -> we.sendMessage(Component.text("One of Nine is marked vulnerable!", NamedTextColor.LIGHT_PURPLE)));
        }

        float health = warlordsNPC.getCurrentHealth();
        phaseOne.initialize(health);
        phaseTwo.initialize(health);
        phaseThree.initialize(health);
        phaseFour.initialize(health);
        phaseFive.initialize(health);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(mapCenter, Sound.ENTITY_WITHER_DEATH, 500f, 0.2f);
        for (int i = 0; i < 4; i++) {
            EffectUtils.strikeLightning(deathLocation, false);
        }
        swordManager.stop();
        centerSwordManager.stop();
        damageController.closeWindow();
        arenaCollapseAbility.stop();
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GRAY;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ONE_OF_NINE;
    }

    @Override
    public Component getDescription() {
        return Component.text("Echoes of the Past", NamedTextColor.DARK_PURPLE);
    }

    private void rainSwordDrop() {
        record Slot(double theta, int startTick, ItemDisplay display) {
        }

        List<Slot> ring = new ArrayList<>();
        World world = warlordsNPC.getWorld();

        double radius = 12;
        int count = 9;
        int delayBetween = 5;     // ticks between swords appearing
        int fallDuration = 6;    // ticks to complete the 90° fall (your old value)

        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.AMBIENT_BASALT_DELTAS_LOOP, 500, 0.7f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 500, 0.7f);

        // Prepare slots with theta and staggered start ticks
        for (int i = 0; i < count; i++) {
            double theta = 2 * Math.PI * i / count;
            int start = i * delayBetween; // sword i starts later
            ring.add(new Slot(theta, start, null));
        }

        new GameRunnable(warlordsNPC.getGame()) {
            int t = 0;

            @Override
            public void run() {
                t++;

                boolean allDone = true;

                for (int i = 0; i < ring.size(); i++) {
                    Slot s = ring.get(i);

                    // Spawn at this sword's start tick
                    if (s.display == null && t >= s.startTick()) {
                        double x = mapCenter.getX() + radius * cos(s.theta());
                        double z = mapCenter.getZ() + radius * Math.sin(s.theta());

                        ItemDisplay d = world.spawn(new Location(world, x, mapCenter.getY() + 2, z), ItemDisplay.class, disp -> {
                            disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                            disp.setBillboard(Display.Billboard.FIXED);

                            // Base orientation: face inward + blade upright (adjust +/−90 if your model needs)
                            Quaternionf faceCenter = new Quaternionf().rotateY((float) (s.theta() + Math.PI));
                            Quaternionf upright = new Quaternionf().rotateX((float) Math.toRadians(+90));
                            Quaternionf baseRight = new Quaternionf(faceCenter).mul(upright);

                            disp.setTransformation(new Transformation(
                                    new Vector3f(0, 0, 0),         // leftRotation (animated later)
                                    baseRight,             // start with identity
                                    new Vector3f(50f, 50f, 50f),
                                    new Quaternionf()                      // rightRotation = base orientation
                            ));
                        });

                        // store back
                        ring.set(i, new Slot(s.theta(), s.startTick(), d));
                    }

                    // If not yet spawned, we're not done
                    if (ring.get(i).display == null) {
                        allDone = false;
                        continue;
                    }

                    // Animate only after startTick
                    int localTicks = t - s.startTick();
                    float p = Math.min(1f, localTicks / (float) fallDuration);
                    // ease-out
                    //p = (float) Math.sin(p * Math.PI * 0.5f);
                    float tilt = (float) Math.toRadians(90) * p;

                    // tangent axis = outward × UP (so the sword falls outward)
                    Vector3f axis = new Vector3f((float) Math.sin(s.theta()), 0f, (float) -cos(s.theta()));
                    Quaternionf left = new Quaternionf().rotateAxis(p, axis); // flip sign if it still goes inward

                    ItemDisplay d = ring.get(i).display;
                    Transformation cur = d.getTransformation();
                    d.setTransformation(new Transformation(
                            new Vector3f(cur.getTranslation()),
                            left,
                            new Vector3f(cur.getScale()),
                            new Quaternionf(cur.getRightRotation())
                    ));

                    if (p < 1f) allDone = false;
                }

                if (allDone) {
                    // (optional) remove all or leave them
                    for (Slot s : ring) {
                        if (s.display != null) s.display.remove();
                    }
                    Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 500, 0.5f);
                    cancel();
                }
            }
        }.runTaskTimer(0, 1);
    }
}
