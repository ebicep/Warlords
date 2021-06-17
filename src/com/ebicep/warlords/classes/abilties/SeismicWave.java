package com.ebicep.warlords.classes.abilties;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SeismicWave extends AbstractAbility {

    private List<List<Location>> fallingBlockLocations = new ArrayList<>();
    private List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();
    private Player owner;
    private List<Player> playersHit = new ArrayList<>();

    public SeismicWave(String name, float minDamageHeal, float maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description, Player owner) {
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

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);
        }

        //INSTANT DMG
        List<Entity> near = player.getNearbyEntities(8.5, 3, 8.5);
        near = Utils.filterOutTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR && Utils.getLookingAtWave(player, (Player) entity)) {
                final Location loc = entity.getLocation();
                final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.15).setY(0.35);
                entity.setVelocity(v);
                Warlords.getPlayer((Player) entity).addHealth(Warlords.getPlayer(player), name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        if (location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            FallingBlock fallingBlock = addFallingBlock(location);
                            customFallingBlocks.add(new CustomFallingBlock(fallingBlock, location.getY() + .25, getOwner(), SeismicWave.this));
                        }
                    }
                    SeismicWave.this.getFallingBlockLocations().remove(fallingBlockLocation);
                    break;
                }


                for (int i = 0; i < customFallingBlocks.size(); i++) {
                    CustomFallingBlock customFallingBlock = customFallingBlocks.get(i);
                    customFallingBlock.setTicksLived(customFallingBlock.getTicksLived() + 1);
                    //TODO fix bug where the blocks dont get removed if ability used near high wall - stuck in block?
                    //System.out.println(customFallingBlock.getCustomFallingBlock().getLocation().getY());
                    //System.out.println(customFallingBlock.getyLevel());
                    if (customFallingBlock.getFallingBlock().getLocation().getY() <= customFallingBlock.getyLevel() || customFallingBlock.getFallingBlock().getTicksLived() > 10) {
                        customFallingBlock.getFallingBlock().remove();
                        customFallingBlocks.remove(i);
                        i--;
                    }
                }

                if (fallingBlockLocations.size() == 0 && customFallingBlocks.size() == 0) {
                    System.out.println("WAVE CANCEL");
                    this.cancel();
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
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

    private FallingBlock addFallingBlock(Location location) {
        if (location.getWorld().getBlockAt(location).getType() != Material.AIR) {
            location.add(0, 1, 0);
        }
        Location blockToGet = location.clone().add(0, -1, 0);
        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() == Material.AIR) {
            blockToGet.add(0, -1, 0);
            if (location.getWorld().getBlockAt(location.clone().add(0, -2, 0)).getType() == Material.AIR) {
                blockToGet.add(0, -1, 0);
            }
        }
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location,
                location.getWorld().getBlockAt(blockToGet).getType(),
                location.getWorld().getBlockAt(blockToGet).getData());
        fallingBlock.setVelocity(new Vector(0, .14, 0));
        fallingBlock.setDropItem(false);
        return fallingBlock;
    }
}