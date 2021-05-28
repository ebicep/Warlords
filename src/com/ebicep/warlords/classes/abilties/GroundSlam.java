package com.ebicep.warlords.classes.abilties;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GroundSlam extends AbstractAbility {

    private List<List<Location>> fallingBlockLocations = new ArrayList<>();
    private List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();
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

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.groundslam.activation", 2, 1);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        if (location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            FallingBlock fallingBlock = addFallingBlock(location);
                            customFallingBlocks.add(new CustomFallingBlock(fallingBlock, location.getY() + .25, getOwner(), GroundSlam.this));
                        }
                    }
                    GroundSlam.this.getFallingBlockLocations().remove(fallingBlockLocation);
                    break;
                }

                if (fallingBlockLocations.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 2);

        new BukkitRunnable() {

            @Override
            public void run() {
                for (int i = 0; i < customFallingBlocks.size(); i++) {
                    CustomFallingBlock customFallingBlock = customFallingBlocks.get(i);
                    customFallingBlock.setTicksLived(customFallingBlock.getTicksLived() + 1);
                    for (Player player : Warlords.getPlayers().keySet()) {
                        if (player != customFallingBlock.getOwner() && player.getGameMode() != GameMode.SPECTATOR) {
                            AbstractAbility ability = customFallingBlock.getAbility();
                            if (!((GroundSlam) ability).getPlayersHit().contains(player) && !Warlords.game.onSameTeam(player, customFallingBlock.getOwner())) {
                                if (player.getLocation().distanceSquared(customFallingBlock.getFallingBlock().getLocation()) < 1.5) {
                                    ((GroundSlam) ability).getPlayersHit().add(player);
                                    final Location loc = player.getLocation();
                                    final Vector v = customFallingBlock.getOwner().getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.25);
                                    player.setVelocity(v);
                                    Warlords.getPlayer(player).addHealth(Warlords.getPlayer(customFallingBlock.getOwner()), ability.getName(), ability.getMinDamageHeal(), ability.getMaxDamageHeal(), ability.getCritChance(), ability.getCritMultiplier());
                                }
                            }
                        }
                    }
                    //TODO fix bug where the blocks dont get removed if ability used near high wall - stuck in block?
                    //System.out.println(customFallingBlock.getCustomFallingBlock().getLocation().getY());
                    //System.out.println(customFallingBlock.getyLevel());
                    if (customFallingBlock.getFallingBlock().getLocation().getY() <= customFallingBlock.getyLevel() || customFallingBlock.getFallingBlock().getTicksLived() > 10) {
                        customFallingBlock.getFallingBlock().remove();
                        customFallingBlocks.remove(i);
                        i--;
                    }
                }

                if (fallingBlockLocations.isEmpty() && customFallingBlocks.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
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
            Location location = new Location(world, Math.floor(x), Math.floor(center.getY()), Math.floor(z));
            //location.setY(world.getHighestBlockYAt(location));
            locations.add(location);
        }
        return locations;
    }

    private boolean sameLocation(Location location1, Location location2) {
        return location1.getX() == location2.getX() && location1.getZ() == location2.getZ();
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

