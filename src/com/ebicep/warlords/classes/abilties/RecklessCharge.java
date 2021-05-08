package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility {

    private List<Player> playersHit = new ArrayList<>();

    public RecklessCharge() {
        super("Reckless Charge", -466, -612, 1, 60, 20, 200,
                "§7Charge forward, dealing §c466\n" +
                        "§7- §c612 §7damage to all enemies\n" +
                        "§7you pass through. Enemies hit are\n" +
                        "§5IMMOBILIZED§7, preventing movement\n" +
                        "§7for §60.5 §7seconds. Charge is reduced\n" +
                        "§7when carrying a flag.");
    }

    @Override
    public void onActivate(Player player) {
        playersHit.clear();

        Location eyeLocation = player.getLocation();
        eyeLocation.setPitch(-10);
        //TODO fix charge, needs to set x/z velcity to zero after travelling x blocks or v velocity isnt x anymore
        if (Warlords.world.getBlockAt(eyeLocation.clone().add(0, -1, 0)).getType() != Material.AIR) {
            System.out.println("Launched on ground");
            //travels 5 blocks
            player.setVelocity(eyeLocation.getDirection().multiply(2.4));
            Warlords.getPlayer(player).setCharged(6 * 6 - 7);
            Warlords.getPlayer(player).setChargeLocation(eyeLocation);//.clone().add(eyeLocation.getDirection().multiply(1)));

        } else {
            System.out.println("Launched in air");
            //travels 7 at peak jump
            player.setVelocity(eyeLocation.getDirection().multiply(1.5));
            Warlords.getPlayer(player).setCharged((int) Math.pow(9 - Utils.getDistance(player, .1) * 2, 2));
            Warlords.getPlayer(player).setChargeLocation(eyeLocation);//.clone().add(eyeLocation.getDirection().multiply(1)));

        }
        // warlordsplayer charged variable
        // check distance from start to "end" every tick
        // check collision of every player
        // if at end
        // set x/z vel to 0
        // charged false

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 1, 1);
        }
    }

    public List<Player> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<Player> playersHit) {
        this.playersHit = playersHit;
    }
}
