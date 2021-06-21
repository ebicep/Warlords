package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility {

    private List<Player> playersHit = new ArrayList<>();

    public RecklessCharge() {
        super("Reckless Charge", -466, -612, 11, 60, 20, 200,
                "§7Charge forward, dealing §c466\n" +
                        "§7- §c612 §7damage to all enemies\n" +
                        "§7you pass through. Enemies hit are\n" +
                        "§5IMMOBILIZED§7, preventing movement\n" +
                        "§7for §60.5 §7seconds. Charge is reduced\n" +
                        "§7when carrying a flag.");
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        playersHit.clear();

        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        Location eyeLocation = player.getLocation();
        eyeLocation.setPitch(-10);
        //.clone().add(eyeLocation.getDirection().multiply(1)));
        if (eyeLocation.getWorld().getBlockAt(eyeLocation.clone().add(0, -1, 0)).getType() != Material.AIR) {
            System.out.println("Launched on ground");
            //travels 5 blocks
            player.setVelocity(eyeLocation.getDirection().multiply(2.4));
            wp.setCharged(6 * 6 - 7);

        } else {
            System.out.println("Launched in air");
            //travels 7 at peak jump
            player.setVelocity(eyeLocation.getDirection().multiply(1.5));
            wp.setCharged((int) Math.pow(9 - Utils.getDistance(player, .1) * 2, 2));

        }
        wp.setChargeLocation(eyeLocation);//.clone().add(eyeLocation.getDirection().multiply(1)));
        // warlordsplayer charged variable
        // check distance from start to "end" every tick
        // check collision of every player
        // if at end
        // set x/z vel to 0
        // charged false

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (warlordsPlayer.getCharged() == 0) {
                    this.cancel();
                }
                List<Entity> playersInside = player.getNearbyEntities(2, 2, 2);
                playersInside.removeAll(((RecklessCharge) warlordsPlayer.getSpec().getRed()).getPlayersHit());
                playersInside = Utils.filterOutTeammates(playersInside, player);
                for (Entity entity : playersInside) {
                    if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                        //TODO add 100 slowness   -   DOESNT WORK NOT BARELY SLOW
                        //Warlords.getPlayer((Player) entity).getSpeed().changeCurrentSpeed("Reckless Charge", -10000, .5 * 20);

                        ((RecklessCharge) warlordsPlayer.getSpec().getRed()).getPlayersHit().add((Player) entity);
                        Warlords.getPlayer((Player) entity).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getRed().getName(), warlordsPlayer.getSpec().getRed().getMinDamageHeal(), warlordsPlayer.getSpec().getRed().getMaxDamageHeal(), warlordsPlayer.getSpec().getRed().getCritChance(), warlordsPlayer.getSpec().getRed().getCritMultiplier());
                    }
                }
                //cancel charge if hit a block, making the player stand still
                if (player.getLocation().distanceSquared(warlordsPlayer.getChargeLocation()) > warlordsPlayer.getCharged() || (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0)) {
                    player.setVelocity(new Vector(0, 0, 0));
                    warlordsPlayer.setCharged(0);
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    public List<Player> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<Player> playersHit) {
        this.playersHit = playersHit;
    }
}
