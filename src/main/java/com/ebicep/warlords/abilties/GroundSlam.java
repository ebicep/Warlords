package com.ebicep.warlords.abilties;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GroundSlam extends AbstractAbility {

    public GroundSlam(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Slam the ground, creating a shockwave\n" +
                "§7around you that deals §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + "\n" +
                "§7damage and knocks enemies back slightly.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();
        List<WarlordsPlayer> playersHit = new ArrayList<>();
        Location location = player.getLocation();

        for (int i = 0; i < 6; i++) {
            fallingBlockLocations.add(getCircle(location, i, (i * ((int) (Math.PI * 2)))));
        }

        fallingBlockLocations.get(0).add(player.getLocation());


        Utils.playGlobalSound(player.getLocation(), "warrior.groundslam.activation", 2, 1);

        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        if (location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                            FallingBlock fallingBlock = addFallingBlock(location.clone());
                            customFallingBlocks.add(new CustomFallingBlock(fallingBlock, wp, GroundSlam.this));
                            WarlordsEvents.addEntityUUID(fallingBlock);
                        }
//                        ParticleEffect.VILLAGER_HAPPY.display(0 , 0 ,0, 0, 10, location.getBlock().getLocation(), 1000);
//                        ParticleEffect.FLAME.display(0 , 0 ,0, 0, 10, location, 1000);

                        //DAMAGE
                        PlayerFilter.entitiesAroundRectangle(location.clone().add(0, -.75, 0), 0.75, 4.5, 0.75)
                                .enemiesOf(wp)
                                .forEach(enemy -> {
                                    if (!playersHit.contains(enemy)) {
                                        playersHit.add(enemy);
                                        final Location loc = enemy.getLocation();
                                        final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.25).setY(0.25);
                                        enemy.setVelocity(v, false);
                                        enemy.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                                    }
                                });
                    }
                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }

                if (fallingBlockLocations.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 2);
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                for (int i = 0; i < customFallingBlocks.size(); i++) {
                    CustomFallingBlock customFallingBlock = customFallingBlocks.get(i);
                    customFallingBlock.setTicksLived(customFallingBlock.getTicksLived() + 1);
                    if (Utils.getDistance(customFallingBlock.getFallingBlock().getLocation(), .05) <= .25 || customFallingBlock.getTicksLived() > 10) {
                        customFallingBlock.getFallingBlock().remove();
                        customFallingBlocks.remove(i);
                        i--;
                    }
                }
                if (fallingBlockLocations.isEmpty() && customFallingBlocks.isEmpty()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 0);

        return true;
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
            locations.add(location);
        }
        return locations;
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
        Material type = location.getWorld().getBlockAt(blockToGet).getType();
        byte data = location.getWorld().getBlockAt(blockToGet).getData();
        if (type == Material.GRASS) {
            if ((int) (Math.random() * 3) == 2) {
                type = Material.DIRT;
                data = 0;
            }
        }
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location,
                type,
                data);
        fallingBlock.setVelocity(new Vector(0, .14, 0));
        fallingBlock.setDropItem(false);
        WarlordsEvents.addEntityUUID(fallingBlock);
        return fallingBlock;
    }
}

