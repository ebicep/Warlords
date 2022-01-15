package com.ebicep.warlords.classes.abilties;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SeismicWave extends AbstractAbility {

    private float velocity = 1.25f;

    public SeismicWave(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Send a wave of incredible force\n" +
                "§7forward that deals §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + "\n" +
                "§7damage to all enemies hit and\n" +
                "knocks them back slightly.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);

        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        List<CustomFallingBlock> customFallingBlocks = new ArrayList<>();

        Location location = player.getLocation();
        for (int i = 0; i < 8; i++) {
            fallingBlockLocations.add(getWave(location, i));
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);
        }

        List<WarlordsPlayer> playersHit = new ArrayList<>();
        for (List<Location> fallingBlockLocation : fallingBlockLocations) {
            for (Location loc : fallingBlockLocation) {
                for (WarlordsPlayer p : PlayerFilter
                        .entitiesAroundRectangle(loc, .6, 4, .6)
                        .aliveEnemiesOf(wp)
                        .excluding(playersHit)
                        .closestFirst(wp)
                ) {
                    playersHit.add(p);
                    final Vector v = player.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(-velocity).setY(0.25);
                    p.setVelocity(v, false);
                    p.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                }
            }
        }
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!wp.getGame().isGameFreeze()) {

                            for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                                for (Location location : fallingBlockLocation) {
                                    if (location.getWorld().getBlockAt(location.clone().add(0, 1, 0)).getType() == Material.AIR) {
                                        FallingBlock fallingBlock = addFallingBlock(location);
                                        customFallingBlocks.add(new CustomFallingBlock(fallingBlock, wp, SeismicWave.this));
                                        WarlordsEvents.addEntityUUID(fallingBlock);
                                    }
                                }
                                fallingBlockLocations.remove(fallingBlockLocation);
                                break;
                            }

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
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 0),
                System.currentTimeMillis()
        );

        return true;
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
        return fallingBlock;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}