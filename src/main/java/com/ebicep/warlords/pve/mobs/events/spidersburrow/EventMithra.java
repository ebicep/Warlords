package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventMithra extends AbstractZombie implements BossMob {

    private static final DecimalFormat TIME_FORMAT = new DecimalFormat("0.0");
    private final List<EventEggSac> eggSacs = new ArrayList<>();
    private boolean entangledStateComplete = false;
    private boolean inEntangledState = false;
    private boolean enragedState = false;

    public EventMithra(Location spawnLocation) {
        super(spawnLocation,
                "Mithra",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                20000,
                0.28f,
                20,
                1200,
                1600
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.LIGHT_PURPLE + "Mithra",
                        ChatColor.WHITE + "The Envoy Queen of Illusion",
                        20, 30, 20
                );
            }
        }

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new Spider(spawnLocation));
        }

        option.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (inEntangledState) {
                    if (!event.getPlayer().equals(warlordsNPC) && !event.getAttacker().equals(warlordsNPC)) {
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
            EffectUtils.playSphereAnimation(loc, hitRadius, ParticleEffect.FLAME, 1);
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
                        100,
                        false
                );
            }
        }

        if (warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .75 && !entangledStateComplete) {
            entangledStateComplete = true;
            inEntangledState = true;
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.SLIME_ATTACK, 2, 1.5f);
            groundSlam();
            new GameRunnable(warlordsNPC.getGame()) {
                private World world = warlordsNPC.getWorld();
                final List<Block> webs = new ArrayList<>();
                int ticksElapsed = 0;

                @Override
                public void run() {
                    if (ticksElapsed == 0) {
                        warlordsNPC.setStunTicks(195);
                        spawnWebs();
                        // TODO check valid spawns
                        spawnEggSacs();
                    }

                    if (ticksElapsed % 2 == 0) {
                        int ticksLeft = 200 - ticksElapsed;
                        option.getGame().onlinePlayers().forEach(playerTeamEntry -> {
                            PacketUtils.sendTitle(
                                    playerTeamEntry.getKey(),
                                    ChatColor.RED + "Entangled",
                                    ChatColor.YELLOW + TIME_FORMAT.format(ticksLeft / 20f),
                                    0, ticksLeft, 0
                            );
                        });
                    }

                    if (++ticksElapsed >= 200) {
                        inEntangledState = false;
                        setBlocks(Material.AIR);
                        // check egg sacs
                        checkUnbrokenEggSacs();
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
                    setBlocks(Material.WEB);
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
                                0,
                                false,
                                false
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

        if (warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .3 && !enragedState) {
            enragedState = true;
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
                            ParticleEffect.VILLAGER_ANGRY.display(
                                    0,
                                    0,
                                    0,
                                    0.1f,
                                    1,
                                    warlordsNPC.getLocation().add(0, 1.75, 0),
                                    500
                            );
                        }
                    }
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * 1.15f;
                }
            });
            warlordsNPC.getSpec().setDamageResistance(warlordsNPC.getSpec().getDamageResistance() + 10);
            warlordsNPC.getSpeed().addBaseModifier(5);
        }
    }

    private void groundSlam() {
        GroundSlam groundSlam = new GroundSlam(1000, 1000, 0, 0, 0, 0);
        groundSlam.setTrueDamage(true);
        groundSlam.setSlamSize(9);
        groundSlam.onActivate(warlordsNPC, null);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.BLACK)
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .build());
        EffectUtils.strikeLightning(deathLocation, false, 2);
    }
}
