package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility {

    private List<Player> playersHit = new ArrayList<>();
    private Location chargeLocation;
    private int charge;

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
    public void onActivate(Player player) {
        playersHit.clear();

        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        Location eyeLocation = player.getLocation();
        eyeLocation.setPitch(-10);

        chargeLocation = eyeLocation.clone();

        if (eyeLocation.getWorld().getBlockAt(eyeLocation.clone().add(0, -1, 0)).getType() != Material.AIR) {
            //travels 5 blocks
            player.setVelocity(eyeLocation.getDirection().multiply(2.5));
            charge = 6 * 6 - 7;

        } else {
            //travels 7 at peak jump
            player.setVelocity(eyeLocation.getDirection().multiply(1.6));
            charge = (int) Math.pow(9 - Utils.getDistance(player, .1) * 2, 2);
        }
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
                if (charge == 0) {
                    this.cancel();
                }

                List<Entity> playersInside = player.getNearbyEntities(2.5, 2, 2.5);
                playersInside.removeAll(((RecklessCharge) warlordsPlayer.getSpec().getRed()).getPlayersHit());
                playersInside = Utils.filterOutTeammates(playersInside, player);
                for (Entity entity : playersInside) {
                    if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                        ((RecklessCharge) warlordsPlayer.getSpec().getRed()).getPlayersHit().add((Player) entity);

                        Warlords.getPlayer((Player) entity).addHealth(warlordsPlayer,
                                warlordsPlayer.getSpec().getRed().getName(),
                                warlordsPlayer.getSpec().getRed().getMinDamageHeal(),
                                warlordsPlayer.getSpec().getRed().getMaxDamageHeal(),
                                warlordsPlayer.getSpec().getRed().getCritChance(),
                                warlordsPlayer.getSpec().getRed().getCritMultiplier());

                        // little scuffed might wanna change
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 17, 50));
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 17, 150));
                    }
                }
                //cancel charge if hit a block, making the player stand still
                if (player.getLocation().distanceSquared(chargeLocation) > charge || (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0)) {
                    player.setVelocity(new Vector(0, 0, 0));
                    charge = 0;
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
