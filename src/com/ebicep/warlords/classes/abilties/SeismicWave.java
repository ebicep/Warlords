package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SeismicWave extends AbstractAbility {

    private List<List<Location>> fallingBlockLocations = new ArrayList<>();
    private Player owner;
    private List<Player> playersHit = new ArrayList<>();

    public SeismicWave(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description, Player owner) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
        this.owner = owner;
    }

    @Override
    public void onActivate(Player player) {
        playersHit.clear();

        Location location = player.getLocation();

        for (int i = 0; i < 9; i++) {
            fallingBlockLocations.add(getWave(location, i));
        }
        Warlords.getWaveArrays().add(this);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 1, 1);
        }
    }

    private List<Location> getWave(Location center, int distance) {
        List<Location> locations = new ArrayList<>();
        Location location = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ());
        location.setDirection(center.getDirection());
        location.setPitch(0);
        locations.add(location.add(location.getDirection().multiply(distance)));
        locations.add(location.clone().add(Utils.getLeftDirection(location).multiply(1)));
        locations.add(location.clone().add(Utils.getLeftDirection(location).multiply(2)));
        locations.add(location.clone().add(Utils.getRightDirection(location).multiply(1)));
        locations.add(location.clone().add(Utils.getRightDirection(location).multiply(2)));
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