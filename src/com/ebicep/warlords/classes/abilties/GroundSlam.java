package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GroundSlam extends AbstractAbility {

    private List<List<Location>> fallingBlockLocations = new ArrayList<>();
    private WarlordsPlayer owner;

    public GroundSlam(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description, WarlordsPlayer owner) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
        this.owner = owner;
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation();

        for (int i = 0; i < 6; i++) {
            fallingBlockLocations.add(getCircle(location, i, (i * ((int) (Math.PI * 2)))));
        }
        Warlords.getGroundSlamArray().add(this);

        //TODO arraylist of locations of falling blocks
        //loop in main then add to arraylist of players hit so you dont rehit a player
        //deal with kb


        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "warrior.groundslam.activation", 1, 1);
        }
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
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            float angle = (float) (i * increment);
            float x = (float) (center.getX() + (radius * Math.cos(angle)));
            float z = (float) (center.getZ() + (radius * Math.sin(angle)));
            Location location = new Location(world, x, center.getY(), z);
            //location.setY(world.getHighestBlockYAt(location));
            locations.add(location);
        }
        return locations;
    }

    public List<List<Location>> getFallingBlockLocations() {
        return fallingBlockLocations;
    }

    public void setFallingBlockLocations(List<List<Location>> fallingBlockLocations) {
        this.fallingBlockLocations = fallingBlockLocations;
    }

    public WarlordsPlayer getOwner() {
        return owner;
    }

    public void setOwner(WarlordsPlayer owner) {
        this.owner = owner;
    }
}

