package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Slam {

    Runnable timer = new Runnable() {
        @SuppressWarnings("deprecation")
        public void run() {
            for (Location loc : getCircle(l, rad, (rad * ((int) (Math.PI * 2))))) {
                FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, loc.getWorld().getBlockAt((int) loc.getX(), loc.getWorld().getHighestBlockYAt(loc) - 1, (int) loc.getZ()).getType(), loc.getWorld().getBlockAt((int) loc.getX(), loc.getWorld().getHighestBlockYAt(loc) - 1, (int) loc.getZ()).getData());
                fb.setVelocity(new Vector(0, .3, 0));
                fb.setDropItem(false);
                loc.getBlock().setType(Material.AIR);
                WarlordsEvents.addEntityUUID(fb.getUniqueId());
            }
            rad++;
            rad = (((rad % 20) == 0) ? 1 : rad);
        }
    };

    private final Location l;
    private int rad = 1;
    private int id;

    public Slam(Location l) {
        this.l = l;
        start(2);
    }

    /**
     * Return A List Of Locations That
     * Make Up A Circle Using A Provided
     * Center, Radius, And Desired Points.
     *
     * @param center
     * @param radius
     * @param amount
     * @return
     */
    private ArrayList<Location> getCircle(Location center, float radius, int amount) {
        World world = center.getWorld();
        double increment = ((2 * Math.PI) / amount);
        ArrayList<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < amount; i++) {
            float angle = (float) (i * increment);
            if (Math.toDegrees(angle) > 40) {
                break;
            }
            float x = (float) (center.getX() + (radius / 2.5 * Math.cos(angle)));
            float z = (float) (center.getZ() + (radius / 2.5 * Math.sin(angle)));
            locations.add(new Location(world, x, center.getY(), z));
            world.spawnEntity(new Location(world, x, center.getY(), z), EntityType.ARROW);
        }
        return locations;
    }

    /**
     * Starts The Timer
     *
     * @param delay
     */
    private void start(int delay) {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Warlords.getInstance(), timer, delay, delay);
    }

    /**
     * Stops The Timer
     */
//    protected void stop() {
//        Bukkit.getScheduler().cancelTask(id);
//    }
}