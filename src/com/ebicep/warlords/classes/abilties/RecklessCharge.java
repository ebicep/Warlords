package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class RecklessCharge extends AbstractAbility {
    public RecklessCharge() {
        super("Reckless Charge", -466, -612, 11, 60, 20, 200, "reckless charge description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location eyeLocation = player.getLocation();
        eyeLocation.setPitch(-10);
        double distance = player.getLocation().getY() - player.getWorld().getHighestBlockYAt(player.getLocation());
        System.out.println(distance);
        //TODO fix charge, needs to set x/z velcity to zero after travelling x blocks or v velocity isnt x anymore
        if (distance == 0) {
            System.out.println("Launched on ground");
            //travels 5 blocks
            player.setVelocity(eyeLocation.getDirection().multiply(2));
        } else {
            System.out.println("Launched in air");
            //travels 7 at peak jump
            player.setVelocity(eyeLocation.getDirection().multiply(1.1));
        }
        // warlordsplayer charged variable
        // check distance from start to "end" every tick
        // check collision of every player
        // if at end
        // set x/z vel to 0
        // charged false
    }
}
