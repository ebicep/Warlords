package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.spider.ArachnoVenari;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventMithra extends AbstractMob implements BossMob {

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("0.0");
    private final List<EventEggSac> eggSacs = new ArrayList<>();
    private boolean entangledStateComplete = false;
    private boolean inEntangledState = false;
    private boolean immolationTriggered = false;

    public EventMithra(Location spawnLocation) {
        super(spawnLocation,
                "Mithra",
                20000,
                0.28f,
                20,
                1200,
                1600
        );
    }

    public EventMithra(
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

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new ArachnoVenari(spawnLocation));
        }

        option.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (inEntangledState) {
                    if (!event.getWarlordsEntity().equals(warlordsNPC) && !event.getAttacker().equals(warlordsNPC)) {
                        return;
                    }
                    if (!event.getAbility().equals("Ground Slam")) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();
        long playerCount = option.getGame().warlordsPlayers().count();
        int hitRadius = 15;

        if (ticksElapsed % 150 == 0) {
            EffectUtils.playSphereAnimation(loc, hitRadius, Particle.FLAME, 1);
            for (WarlordsEntity knockTarget : PlayerFilter
                    .entitiesAround(warlordsNPC, hitRadius, hitRadius, hitRadius)
                    .aliveEnemiesOf(warlordsNPC)
                    .closestFirst(warlordsNPC)
            ) {
                EffectUtils.strikeLightning(knockTarget.getLocation(), false);
                knockTarget.setVelocity(name, new Vector(0, 1, 0), false);
                knockTarget.addDamageInstance(
                        warlordsNPC,
                        "Virtue Strike",
                        400 * playerCount,
                        500 * playerCount,
                        0,
                        100
                );
            }
        }

        if (warlordsNPC.getCurrentHealth() <= warlordsNPC.getMaxBaseHealth() * .75 && !entangledStateComplete) {
            entangledStateComplete = true;
            inEntangledState = true;
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_SLIME_ATTACK, 2, 1.5f);
            groundSlam();
            new GameRunnable(warlordsNPC.getGame()) {
                final List<Block> webs = new ArrayList<>();
                int ticksElapsed = 0;
                private World world = warlordsNPC.getWorld();

                @Override
                public void run() {
                    if (ticksElapsed == 0) {
                        warlordsNPC.setStunTicks(195);
                        spawnWebs();
                        spawnEggSacs();
                    }

                    if (ticksElapsed % 2 == 0) {
                        int ticksLeft = 200 - ticksElapsed;
                        ChatUtils.sendTitleToGamePlayers(
                                getWarlordsNPC().getGame(),
                                Component.text("Entangled", NamedTextColor.RED),
                                Component.text(TIME_FORMAT.format(ticksLeft / 20f), NamedTextColor.YELLOW),
                                0, ticksLeft, 0
                        );
                    }

                    if (++ticksElapsed >= 200) {
                        inEntangledState = false;
                        setBlocks(Material.AIR);
                        // check egg sacs
                        checkUnbrokenEggSacs();
                        enrage();
                        this.cancel();
                    }

                }

                private void spawnWebs() {
                    List<Location> cube = getCubeLocations();
                    cube.forEach(location -> {
                        Block blockAt = world.getBlockAt(location);
                        if (blockAt.getType() == Material.AIR) {
                            webs.add(blockAt);
                        }
                    });
                    setBlocks(Material.COBWEB);
                }

                private void spawnEggSacs() {
                    List<Location> spawnLocations = new ArrayList<>();
                    spawnLocations.add(new Location(world, 11.5, 22, 3.5, 135, 0));
                    spawnLocations.add(new Location(world, 11.5, 22, -8.5, 45, 0));
                    spawnLocations.add(new Location(world, -3.5, 22, -8.5, -45, 0));
                    spawnLocations.add(new Location(world, -3.5, 22, 3.5, -135, 0));
                    Collections.shuffle(spawnLocations);
                    for (int i = 0; i < playerCount; i++) {
                        Location spawnLocation = spawnLocations.remove(0);
                        EventEggSac eggSac = new EventEggSac(spawnLocation);
                        eggSacs.add(eggSac);
                        option.spawnNewMob(eggSac);
                    }
                }

                private void setBlocks(Material material) {
                    for (Block b : webs) {
                        b.setType(material);
                    }
                }

                private void checkUnbrokenEggSacs() {
                    float healthGain = warlordsNPC.getMaxBaseHealth() * .05f;
                    for (EventEggSac eggSac : eggSacs) {
                        if (!option.getMobs().contains(eggSac)) {
                            continue;
                        }
                        WarlordsNPC eggSacWarlordsNPC = eggSac.getWarlordsNPC();
                        eggSacWarlordsNPC.die(eggSacWarlordsNPC);
                        Location location = eggSacWarlordsNPC.getLocation();
                        if (EventEggSac.ARMOR_STAND) {
                            location.add(0, 1.31, 0);
                        }
                        for (int i = 0; i < 3; i++) {
                            option.spawnNewMob(new EventPoisonousSpider(location));
                        }
                        warlordsNPC.addHealingInstance(
                                warlordsNPC,
                                "Entangled",
                                healthGain,
                                healthGain,
                                0,
                                0
                        );
                    }
                }

                @Nonnull
                private List<Location> getCubeLocations() {
                    List<Location> cube = new ArrayList<>();
                    LocationBuilder startingCorner = new LocationBuilder(warlordsNPC.getLocation())
                            .yaw(0)
                            .pitch(0)
                            .backward(3)
                            .left(2);
                    //cube.add(startingCorner.clone());
                    for (int y = 0; y < 4; y++) {
                        for (int i = 0; i < 5; i++) {
                            for (int k = 0; k < 5; k++) {
                                startingCorner.forward(1);
                                cube.add(startingCorner.clone());
                            }
                            startingCorner.backward(5);
                            startingCorner.right(1);
                        }
                        startingCorner.left(5);
                        startingCorner.addY(1);
                    }
                    return cube;
                }
            }.runTaskTimer(0, 0);
        }

        if (warlordsNPC.getCurrentHealth() <= warlordsNPC.getMaxBaseHealth() * .35 && !immolationTriggered) {
            immolationTriggered = true;
            immolation(option, warlordsNPC.getLocation());
        }
    }

    private void groundSlam() {
        AbstractGroundSlam groundSlam = new AbstractGroundSlam(1000, 1000, 0, 0, 0, 0) {{
            setTrueDamage(true);
            getHitBoxRadius().setBaseValue(9);
        }};
        groundSlam.onActivate(warlordsNPC);
    }

    private void enrage() {
        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.berserk.activation", 2, .5f);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Enraged",
                null,
                EventMithra.class,
                null,
                warlordsNPC,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                false,
                (cooldown, ticks) -> {
                    if (ticks % 3 == 0) {
                        Location location = warlordsNPC.getLocation();
                        location.getWorld().spawnParticle(
                                Particle.VILLAGER_ANGRY,
                                location,
                                1,
                                0,
                                0,
                                0,
                                0.1f,
                                null,
                                true
                        );
                    }
                }
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.15f;
            }
        });
        warlordsNPC.setDamageResistance(warlordsNPC.getSpec().getDamageResistance() + 10);
        warlordsNPC.getSpeed().addBaseModifier(5);
    }

    private void immolation(PveOption option, Location loc) {
        warlordsNPC.addSpeedModifier(warlordsNPC, "Mithra Slowness", -99, 250);
        for (int i = 0; i < 3; i++) {
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 500, 0.6f);
        }

        ChatUtils.sendTitleToGamePlayers(
                getWarlordsNPC().getGame(),
                Component.text("PREPARE TO DIE", NamedTextColor.RED),
                Component.text("Immolation Spell", NamedTextColor.LIGHT_PURPLE),
                20, 60, 20
        );

        float damage = switch (option.getDifficulty()) {
            case ENDLESS, HARD -> 200;
            case EXTREME -> 250;
            case EASY -> 50;
            default -> 100;
        };
        new GameRunnable(warlordsNPC.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                    return;
                }

                counter++;
                double radius = (2 * counter);
                Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 500, 0.8f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 500, 0.6f);
                EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), radius, Particle.FLAME, 2, counter);
                for (WarlordsEntity flameTarget : PlayerFilter
                        .entitiesAround(warlordsNPC, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    Utils.addKnockback(name, warlordsNPC.getLocation(), flameTarget, -1, 0.1f);
                    flameTarget.addDamageInstance(
                            warlordsNPC,
                            "Immolation",
                            damage,
                            damage,
                            0,
                            100
                    );

                    warlordsNPC.addHealingInstance(
                            warlordsNPC,
                            "Immolation",
                            damage * 0.5f,
                            damage * 0.5f,
                            0,
                            100
                    );
                }

                if (counter == 50) {
                    this.cancel();
                    warlordsNPC.getSpeed().addBaseModifier(70);
                }
            }
        }.runTaskTimer(40, 5);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.BLACK)
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .build());
        EffectUtils.strikeLightning(deathLocation, false, 2);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA;
    }

    @Override
    public Component getDescription() {
        return Component.text("The Envoy Queen of Illusion", NamedTextColor.WHITE);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }
}
