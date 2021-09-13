package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility {

    public RecklessCharge() {
        super("Reckless Charge", -466, -612, 9.98f, 60, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Charge forward, dealing §c" + -minDamageHeal + "\n" +
                "§7- §c" + -maxDamageHeal + " §7damage to all enemies\n" +
                "§7you pass through. Enemies hit are\n" +
                "§5IMMOBILIZED§7, preventing movement\n" +
                "§7for §60.5 §7seconds. Charge is reduced\n" +
                "§7when carrying a flag.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        Location eyeLocation = player.getLocation();
        eyeLocation.setPitch(0);

        Location chargeLocation = eyeLocation.clone();
        double chargeDistance;
        List<WarlordsPlayer> playersHit = new ArrayList<>();

        BukkitTask runnable;
        if (eyeLocation.getWorld().getBlockAt(eyeLocation.clone().add(0, -1, 0)).getType() != Material.AIR) {
            //travels 5 blocks
//            runnable = new BukkitRunnable() {
//
//                @Override
//                public void run() {
//                    player.setVelocity(eyeLocation.getDirection().multiply(1.4).setY(.3));
//                }
//            }.runTaskTimer(Warlords.getInstance(), 0 ,0);
            player.setVelocity(eyeLocation.getDirection().multiply(3).setY(.3));
            chargeDistance = 5;
        } else {
            //travels 7 at peak jump
//            runnable = new BukkitRunnable() {
//
//                @Override
//                public void run() {
//                    player.setVelocity(eyeLocation.getDirection().multiply(1.4).setY(.15));
//                }
//            }.runTaskTimer(Warlords.getInstance(), 0 ,0);
            player.setVelocity(eyeLocation.getDirection().multiply(2).setY(.3));
            chargeDistance = Math.max(Math.min(Utils.getDistance(player, .1) * 5, 6.9), 6);
        }

        Location target = new LocationBuilder(eyeLocation).forward((float) chargeDistance).get();
        //player.setVelocity(player.getLocation().toVector().subtract(target.toVector()).normalize().multiply(-2));
//        System.out.println("----------");
//        System.out.println(eyeLocation);
//        System.out.println(eyeLocation.getDirection());
//        System.out.println(player.getLocation().toVector().subtract(target.toVector()).normalize().multiply(-2));
        if (wp.getGameState().flags().hasFlag(wp)) {
            chargeDistance /= 4;
        }

//        System.out.println(Utils.getDistance(player, .1));
//        System.out.println(chargeDistance);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);
        }

//        ParticleEffect.VILLAGER_HAPPY.display(0 , 0 ,0, 0, 10, target, 1000);


        double finalChargeDistance = chargeDistance;
        new BukkitRunnable() {

            @Override
            public void run() {
                //cancel charge if hit a block, making the player stand still
                if (player.getLocation().distanceSquared(chargeLocation) > finalChargeDistance * finalChargeDistance ||
                        (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0)) {
//                    runnable.cancel();
                    player.setVelocity(new Vector(0, 0, 0));
                    this.cancel();
                }
                PlayerFilter.entitiesAround(player, 2.25, 5, 2.25)
                        .excluding(playersHit)
                        .aliveEnemiesOf(wp)
                        .forEach(enemy -> {
                            playersHit.add(enemy);
                            enemy.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                            new BukkitRunnable() {
                                final Location stunLocation = enemy.getLocation();
                                int timer = 0;

                                @Override
                                public void run() {
                                    stunLocation.setPitch(enemy.getEntity().getLocation().getPitch());
                                    stunLocation.setYaw(enemy.getEntity().getLocation().getYaw());
                                    enemy.teleport(stunLocation);
                                    //.5 seconds
                                    if (timer >= 10) {
                                        this.cancel();
                                    }
                                    timer++;
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 0);
                            PacketUtils.sendTitle((Player) enemy.getEntity(), "", "§dIMMOBILIZED", 0, 10, 0);
                        });
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }
}
