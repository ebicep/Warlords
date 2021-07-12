package com.ebicep.warlords.classes.abilties;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GroundSlam extends AbstractAbility {

    private List<List<Location>> fallingBlockLocations = new ArrayList<>();
    private final List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();
    private final List<WarlordsPlayer> playersHit = new ArrayList<>();

    public GroundSlam(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Slam the ground, creating a shockwave\n" +
                "§7around you that deals §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + "\n" +
                "§7damage and knocks enemies back slightly.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        playersHit.clear();
        fallingBlockLocations.clear();
        customFallingBlocks.clear();

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
                            customFallingBlocks.add(new CustomFallingBlock(fallingBlock, location.getY() + .25, wp, GroundSlam.this));
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

                    for (WarlordsPlayer player : PlayerFilter.playingGame(wp.getGame()).isAlive()) {
                        if (player != customFallingBlock.getOwner()) {
                            AbstractAbility ability = customFallingBlock.getAbility();
                            if (!((GroundSlam) ability).getPlayersHit().contains(player) && player.isEnemy(customFallingBlock.getOwner())) {
                                if (player.getLocation().distanceSquared(customFallingBlock.getFallingBlock().getLocation()) < 3) {
                                    ((GroundSlam) ability).getPlayersHit().add(player);
                                    final Location loc = player.getLocation();
                                    final Vector v = customFallingBlock.getOwner().getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.15).setY(0.35);
                                    player.setVelocity(v);
                                    Warlords.getPlayer(player).addHealth(Warlords.getPlayer(customFallingBlock.getOwner()), ability.getName(), ability.getMinDamageHeal(), ability.getMaxDamageHeal(), ability.getCritChance(), ability.getCritMultiplier());
                                }
                            }
                        }
                    }
                    if (customFallingBlock.getFallingBlock().getLocation().getY() <= customFallingBlock.getyLevel() || customFallingBlock.getTicksLived() > 10) {
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

    public List<WarlordsPlayer> getPlayersHit() {
        return playersHit;
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
        WarlordsEvents.addEntityUUID(fallingBlock.getUniqueId());
        return fallingBlock;
    }
}

