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
    private Player owner;
    private List<Player> playersHit = new ArrayList<>();

    public GroundSlam(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description, Player owner) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
        this.owner = owner;
    }

    @Override
    public void onActivate(Player player) {
        playersHit.clear();
        Location location = player.getLocation();

        for (int i = 0; i < 6; i++) {
            fallingBlockLocations.add(getCircle(location, i, (i * ((int) (Math.PI * 2)))));
        }
        Warlords.getGroundSlamArray().add(this);

        for (Player player1 : player.getWorld().getPlayers()) {
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
    private List<Location> getCircle(Location center, float radius, int amount) {
        World world = center.getWorld();
        double increment = ((2 * Math.PI) / amount);
        List<Location> locations = new ArrayList<>();
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

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public List<Player> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<Player> playersHit) {
        this.playersHit = playersHit;
    }
}

