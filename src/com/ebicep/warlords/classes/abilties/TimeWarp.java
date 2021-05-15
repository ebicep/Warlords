package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TimeWarp extends AbstractAbility {

    public TimeWarp() {
        super("Time Warp", 0, 0, 29, 30, 0, 0,
                "§7Activate to place a time rune on\n" +
                "§7the ground. After §65 §7seconds,\n" +
                "§7you will warp back to that location\n" +
                "§7and restore §a30% §7of your health");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        Warlords.getTimeWarpPlayers().add(new TimeWarpPlayer(warlordsPlayer, player.getLocation(), player.getLocation().getDirection(), 5));
        warlordsPlayer.getActionBarStats().add(new ActionBarStats(warlordsPlayer, "TIME", 5));
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.timewarp.activation", 1, 1);
        }
    }

    public static class TimeWarpPlayer {

        private WarlordsPlayer warlordsPlayer;
        private Location location;
        private Vector facing;
        private int time;

        public TimeWarpPlayer(WarlordsPlayer warlordsPlayer, Location location, Vector facing, int time) {
            this.warlordsPlayer = warlordsPlayer;
            this.location = location;
            this.facing = facing;
            this.time = time;
        }

        public WarlordsPlayer getWarlordsPlayer() {
            return warlordsPlayer;
        }

        public void setWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
            this.warlordsPlayer = warlordsPlayer;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Vector getFacing() {
            return facing;
        }

        public void setFacing(Vector facing) {
            this.facing = facing;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}
