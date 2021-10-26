package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecklessCharge extends AbstractAbility implements Listener {

    private static List<UUID> stunnedPlayers = new ArrayList<>();

    public RecklessCharge() {
        super("Reckless Charge", -457, -601, 9.32f, 60, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Charge forward, dealing §c" + format(-minDamageHeal) + "\n" +
                "§7- §c" + format(-maxDamageHeal) + " §7damage to all enemies\n" +
                "§7you pass through. Enemies hit are\n" +
                "§5IMMOBILIZED§7, preventing movement\n" +
                "§7for §60.5 §7seconds. Charge is reduced\n" +
                "§7when carrying a flag.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Location location = player.getLocation();
        location.setPitch(0);

        Location chargeLocation = location.clone();
        double chargeDistance;
        List<WarlordsPlayer> playersHit = new ArrayList<>();

        boolean inAir = false;
        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() != Material.AIR) {
            inAir = true;
            //travels 5 blocks
            chargeDistance = 5;
        } else {
            //travels 7 at peak jump
            chargeDistance = Math.max(Math.min(Utils.getDistance(player, .1) * 5, 6.9), 6);
        }
        if (wp.getGameState().flags().hasFlag(wp)) {
            chargeDistance /= 4;
        }

        boolean finalInAir = inAir;

        if (finalInAir) {
            wp.getGame().getGameTasks().put(

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            player.setVelocity(location.getDirection().multiply(2).setY(.2));
                        }
                    }.runTaskLater(Warlords.getInstance(), 0),
                    System.currentTimeMillis()
            );
        } else {
            player.setVelocity(location.getDirection().multiply(1.5).setY(.2));
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);
        }

        double finalChargeDistance = chargeDistance;
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    //safety precaution
                    int maxChargeDuration = 5;

                    @Override
                    public void run() {
                        //cancel charge if hit a block, making the player stand still
                        if (player.getLocation().distanceSquared(chargeLocation) > finalChargeDistance * finalChargeDistance ||
                                (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0) ||
                                maxChargeDuration <= 0
                        ) {
                            player.setVelocity(new Vector(0, 0, 0));
                            this.cancel();
                        }
                        for (int i = 0; i < 4; i++) {
                            ParticleEffect.REDSTONE.display(
                                    new ParticleEffect.OrdinaryColor(255, 0, 0),
                                    player.getLocation().clone().add((Math.random() * 1.5) - .75, .5 + (Math.random() * 2) - 1, (Math.random() * 1.5) - .75),
                                    500);
                        }
                        PlayerFilter.entitiesAround(player, 2.4, 5, 2.4)
                                .excluding(playersHit)
                                .aliveEnemiesOf(wp)
                                .forEach(enemy -> {
                                    playersHit.add(enemy);
                                    stunnedPlayers.add(enemy.getUuid());
                                    enemy.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                                    wp.getGame().getGameTasks().put(

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    stunnedPlayers.remove(enemy.getUuid());
                                                }
                                            }.runTaskLater(Warlords.getInstance(), 7),
                                            System.currentTimeMillis()
                                    ); //.35 seconds
                                    if (enemy.getEntity() instanceof Player) {
                                        PacketUtils.sendTitle((Player) enemy.getEntity(), "", "§dIMMOBILIZED", 0, 10, 0);
                                    }
                                });

                        maxChargeDuration--;
                    }

                }.runTaskTimer(Warlords.getInstance(), 1, 0),
                System.currentTimeMillis()
        );
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (stunnedPlayers.contains(e.getPlayer().getUniqueId())) {
            e.setTo(e.getFrom());
        }
    }
}
