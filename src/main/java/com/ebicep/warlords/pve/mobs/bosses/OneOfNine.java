package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.raid.BossAbilityPhase;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.OrbitingSwordsManager;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.EchoOfBlades;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.PhysiraCrystal;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SoulOfGradient;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
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
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class OneOfNine extends AbstractMob implements BossMob {

    private Listener listener;
    List<WarlordsEntity> pylons = new ArrayList<>();
    private Location mapCenter;
    private OrbitingSwordsManager swordManager;
    private OrbitingSwordsManager centerSwordManager;
    private BossAbilityPhase phaseOne;
    private BossAbilityPhase phaseTwo;
    private BossAbilityPhase phaseThree;
    private BossAbilityPhase phaseFour;
    private BossAbilityPhase phaseFive;

    public OneOfNine(Location spawnLocation) {
        super(
                spawnLocation,
                "One of Nine",
                250000,
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

        EffectUtils.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.GRAY)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.strikeLightningInCylinder(warlordsNPC.getLocation(), 10, false);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_6, 10, 0.5f);

        swordManager = new OrbitingSwordsManager(() -> warlordsNPC.getLocation(), 6, 2, 3, 4, option, warlordsNPC);
        centerSwordManager = new OrbitingSwordsManager(() -> mapCenter, 25, 30, 1, 30, option, warlordsNPC);

        swordManager.spawnSwords(9);
        swordManager.start();

        centerSwordManager.spawnSwords(9);
        centerSwordManager.start();

        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5);

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(warlordsNPC, 6, 6, 6)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    we.addInstance(InstanceBuilder
                            .damage()
                            .min(800)
                            .max(1200)
                            .source(warlordsNPC)
                            .cause("Reaving Blades")
                            .flag(InstanceFlags.TRUE_DAMAGE, true)
                    );
                }
                if (warlordsNPC.isDead()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 10);

        phaseOne = new BossAbilityPhase(warlordsNPC, 90, () -> {
            record Slot(double theta, int startTick, ItemDisplay display) {}

            List<Slot> ring = new ArrayList<>();
            World world = warlordsNPC.getWorld();

            double radius = 12;
            int count = 9;
            int delayBetween = 20;     // ticks between swords appearing
            int fallDuration = 8;    // ticks to complete the 90° fall (your old value)

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

                @Override public void run() {
                    t++;

                    boolean allDone = true;

                    for (int i = 0; i < ring.size(); i++) {
                        Slot s = ring.get(i);

                        // Spawn at this sword's start tick
                        if (s.display == null && t >= s.startTick()) {
                            double x = mapCenter.getX() + radius * Math.cos(s.theta());
                            double z = mapCenter.getZ() + radius * Math.sin(s.theta());

                            ItemDisplay d = world.spawn(new Location(world, x, mapCenter.getY() + 2, z), ItemDisplay.class, disp -> {
                                disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                                disp.setBillboard(Display.Billboard.FIXED);

                                // Base orientation: face inward + blade upright (adjust +/−90 if your model needs)
                                Quaternionf faceCenter = new Quaternionf().rotateY((float) (s.theta() + Math.PI));
                                Quaternionf upright    = new Quaternionf().rotateX((float) Math.toRadians(+90));
                                Quaternionf baseRight  = new Quaternionf(faceCenter).mul(upright);

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
                        if (ring.get(i).display == null) { allDone = false; continue; }

                        // Animate only after startTick
                        int localTicks = t - s.startTick();
                        float p = Math.min(1f, localTicks / (float) fallDuration);
                        // ease-out
                        p = (float) Math.sin(p * Math.PI * 0.5f);
                        float tilt = (float) Math.toRadians(90) * p;

                        // tangent axis = outward × UP (so the sword falls outward)
                        Vector3f axis = new Vector3f((float) Math.sin(s.theta()), 0f, (float) -Math.cos(s.theta()));
                        Quaternionf left = new Quaternionf().rotateAxis(+tilt, axis); // flip sign if it still goes inward

                        ItemDisplay d = ring.get(i).display;
                        Transformation cur = d.getTransformation();
                        d.setTransformation(new Transformation(
                                new Vector3f(cur.getTranslation()),
                                left,
                                new Vector3f(cur.getScale()),
                                new Quaternionf(cur.getRightRotation())
                        ));

                        // finished?
                        if (p < 1f) allDone = false;
                    }

                    if (allDone) {
                        // (optional) remove all or leave them
                        for (Slot s : ring) {
                            if (s.display != null) s.display.remove();
                        }
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 500, 0.5f);
                        swordManager.removeNextSword();
                        centerSwordManager.removeNextSword();
                        cancel();
                    }
                }
            }.runTaskTimer(0, 1);

            phaseTransition();
        });

        phaseTwo = new BossAbilityPhase(warlordsNPC, 70, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Destroy the Nine ornaments before the timer runs out!", NamedTextColor.GRAY),
                    20,
                    60,
                    20
            );

            Location crystalLoc = mapCenter.clone();
            for (int j = 0; j < 9; j++) {
                double angle = j / 9D * Math.PI * 2;
                crystalLoc.setX(mapCenter.getX() + Math.sin(angle) * 20);
                crystalLoc.setZ(mapCenter.getZ() + cos(angle) * 20);
                PhysiraCrystal crystal = new PhysiraCrystal(crystalLoc, warlordsNPC, SpecType.VALUES[j % 3]);
                pylons.add(crystal.getWarlordsNPC());
                pveOption.spawnNewMob(crystal, Team.RED);
            }

            listener = new Listener() {
                @EventHandler(ignoreCancelled = true)
                private void onAllyDeath(WarlordsDeathEvent event) {
                    pylons.removeFirst();
                }
            };

            warlordsNPC.getGame().registerEvents(listener);

            AtomicInteger countdown = new AtomicInteger(30);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    if (counter % 20 == 0) {
                        countdown.getAndDecrement();
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.5f);
                    }

                    if (pylons.isEmpty() && countdown.get() > 0) {
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.WHITE)
                                        .withTrail()
                                        .build()
                        );

                        swordManager.removeNextSword();
                        centerSwordManager.removeNextSword();

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
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("The floor is filled with despair...", NamedTextColor.DARK_RED),
                    20,
                    60,
                    20
            );
            new GameRunnable(warlordsNPC.getGame()) {
                double angle = 0;
                @Override
                public void run() {
                    angle += Math.toRadians(5); // rotation speed
                    Vector dir = new Vector(Math.cos(angle), 0, Math.sin(angle));
                    castDeathRay(mapCenter.clone().add(0, -2, 0), dir, 35, warlordsNPC);

                    if (angle > 90) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(60, 1); // every 2 ticks

            phaseTransition();
        });

        phaseFour = new BossAbilityPhase(warlordsNPC, 30, () -> {
            // zone control
        });

        phaseFive = new BossAbilityPhase(warlordsNPC, 10, () -> {
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
                    return currentDamageValue * 0.5f;
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

            // Damage players in the path
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(point, 1.5, 1, 1.5)
                    .aliveEnemiesOf(caster)
            ) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .cause("Death Ray")
                        .value(1000)
                        .source(warlordsNPC)
                );
            }
        }
    }

    public void phaseTransition() {
        new FallingBlockWaveEffect(
                mapCenter.clone().add(0, 1, 0),
                20,
                0.7,
                Material.SOUL_FIRE
        ).play();

        Utils.playGlobalSound(mapCenter, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
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
            Utils.addKnockback("One of Nine", warlordsNPC.getLocation(), we, -20, 0.05);
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

        if (ticksElapsed % 800 == 0 && ticksElapsed > 0) {
            for (int i = 0; i < option.playerCount(); i++) {
                option.spawnNewMob(new EchoOfBlades(pveOption.getRandomSpawnLocation(warlordsNPC)));
            }
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

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);

        swordManager.stop();
        centerSwordManager.stop();
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
}
