package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EventMithra extends AbstractZombie implements BossMob {

    private boolean entangledState = false;
    private boolean enragedState = false;
    private List<EventEggSac> eggSacs = new ArrayList<>();

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
                if (entangledState) {
                    if (event.getPlayer().equals(warlordsNPC) || event.getAttacker().equals(warlordsNPC)) {
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

        if (warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .75 && !entangledState) {
            entangledState = true;
            GroundSlam groundSlam = new GroundSlam(1000, 1000, 0, 0, 0, 0);
            groundSlam.setTrueDamage(true);
            groundSlam.onActivate(warlordsNPC, null);
            new GameRunnable(warlordsNPC.getGame()) {
                int ticksElapsed = 0;
                final List<Block> webs = new ArrayList<>();

                @Override
                public void run() {
                    if (ticksElapsed == 0) {
                        List<Location> cube = new ArrayList<>();
                        LocationBuilder startingCorner = new LocationBuilder(warlordsNPC.getLocation())
                                .backward(2)
                                .left(2);
                        cube.add(startingCorner.clone());
                        for (int y = 0; y < 5; y++) {
                            for (int i = 0; i < 5; i++) {
                                for (int k = 0; k < 5; k++) {
                                    startingCorner.forward(1);
                                    cube.add(startingCorner.clone());
                                }
                                startingCorner.backward(5);
                                startingCorner.right(1);
                            }
                            startingCorner.addY(1);
                        }
                        cube.forEach(location -> {
                            Block blockAt = warlordsNPC.getWorld().getBlockAt(location);
                            if (blockAt.getType() != Material.AIR) {
                                webs.add(blockAt);
                            }
                        });
                        for (Block block : webs) {
                            block.setType(Material.WEB);
                        }

                        // TODO check valid spawns
                        LocationBuilder spawnLocation = new LocationBuilder(warlordsNPC.getLocation());
                        for (int i = 0; i < playerCount; i++) {
                            spawnLocation.pitch(ThreadLocalRandom.current().nextInt(0, 360));
                            spawnLocation.forward(2);
                            EventEggSac eggSac = new EventEggSac(spawnLocation);
                            eggSacs.add(eggSac);
                            option.spawnNewMob(eggSac);
                        }
                    }

                    int ticksLeft = 200 - ticksElapsed;
                    for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
                        if (we.getEntity() instanceof Player) {
                            PacketUtils.sendTitle(
                                    (Player) we.getEntity(),
                                    ChatColor.RED + "Entangled",
                                    ChatColor.YELLOW.toString() + ticksLeft / 20f,
                                    0, ticksLeft, 0
                            );
                        }
                    }

                    if (++ticksElapsed >= 200) {
                        entangledState = false;
                        for (Block b : webs) {
                            b.setType(Material.AIR);
                        }
                        // check egg sacs
                        float healthGain = warlordsNPC.getMaxBaseHealth() * .05f;
                        for (EventEggSac eggSac : eggSacs) {
                            if (option.getMobs().contains(eggSac)) {
                                WarlordsNPC eggSacWarlordsNPC = eggSac.getWarlordsNPC();
                                Location location = eggSacWarlordsNPC.getLocation();
                                eggSacWarlordsNPC.die(eggSacWarlordsNPC);
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
                        this.cancel();
                    }

                }
            }.runTaskTimer(30, 0);
        }

        if (warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .3 && !enragedState) {
            enragedState = true;

        }
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
