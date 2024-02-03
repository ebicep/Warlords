package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.towerdefense.towers.PyroTower;
import com.ebicep.warlords.game.option.towerdefense.towers.Tower;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public class TowerBuildOption implements Option {

    private static final EnumSet<Material> BUILDABLE = EnumSet.of(Material.OAK_PLANKS);
    private static final ItemStack BUILD_TOWER_ITEM = new ItemBuilder(Material.STICK)
            .name(Component.text("Build Tower", NamedTextColor.GREEN))
            .setOnUseID("BUILD_TOWER_ITEM")
            .setPlaceableOn(BUILDABLE)
            .get();
    private final List<Tower> builtTowers = new ArrayList<>();
    private final Map<UUID, Long> playerBuildCooldowns = new HashMap<>();
    private boolean debug = false;

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onInteract(PlayerInteractEvent event) {
                Action action = event.getAction();
                if (action != Action.RIGHT_CLICK_BLOCK) {
                    return;
                }
                ItemStack itemInHand = event.getItem();
                if (itemInHand == null) {
                    return;
                }
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock == null) {
                    return;
                }
                Player player = event.getPlayer();
                WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
                if (warlordsEntity == null) {
                    return;
                }
                if (!warlordsEntity.getGame().equals(game)) {
                    return;
                }

                ItemMeta itemMeta = itemInHand.getItemMeta();
                if (itemMeta == null) {
                    return;
                }
                String onUseID = itemMeta.getPersistentDataContainer().get(ItemBuilder.ON_USE_NAMESPACED_KEY, PersistentDataType.STRING);
                if (!Objects.equals(onUseID, "BUILD_TOWER_ITEM")) {
                    return;
                }
                if (playerBuildCooldowns.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
//                    player.sendMessage(Component.text("You can't build a tower yet!", NamedTextColor.RED));
                    return;
                }
                BuildResult buildResult = buildTower(clickedBlock.getLocation(), player.getLocation(), new PyroTower());// TODO
//                player.sendMessage(Component.text("Build Result: " + buildResult.name(), NamedTextColor.GREEN));
                if (buildResult == BuildResult.SUCCESS) {
                    playerBuildCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                }
            }

        });
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        player.getInventory().setItem(6, BUILD_TOWER_ITEM);
    }

    private BuildResult buildTower(Location location, @NotNull Location playerLocation, Tower tower) {
        Location alignedLocation = location.clone();
        BuildResult buildResult = getAlignedLocation(alignedLocation, playerLocation, tower);
        if (buildResult == BuildResult.SUCCESS) {
            alignedLocation.setYaw(0);
            tower.build(alignedLocation.add(0, 1, 0)); // TODO decide when to add to y
            builtTowers.add(tower);
        }
        return buildResult;
    }

    /**
     * <p>Parameter can be any location with the block matching any buildable material (will be modified to be the corner).
     * Aligns to front right corner facing south.</p>
     * <li>First get size of plot.</li>
     * <li>If plot size < tower size then cant build.</li>
     * <li>If plot size == tower size then build</li>
     * <li>If plot size > tower size AND tower can be built at location, then build at bottom right relative to that location</li>
     * <li>Else cant build</li>
     *
     * @param location       the location to align
     * @param playerLocation the location of the player
     * @param tower          the tower to align
     * @return null if location is invalid, else the aligned location
     */
    private BuildResult getAlignedLocation(Location location, @NotNull Location playerLocation, Tower tower) {
        Block block = location.getBlock();
        Material type = block.getType();
        if (!BUILDABLE.contains(type)) {
            return BuildResult.INVALID_LOCATION;
        }
        location.setPitch(0);

        LocationBuilder bottomRightCorner = new LocationBuilder(location).yaw(0); // facing south
        alignToBottomRightCorner(tower, type, bottomRightCorner);

        debugParticle(location.clone().add(.5, 1, .5), Particle.VILLAGER_HAPPY);
        debugParticle(bottomRightCorner.clone().add(.5, 1, .5), Particle.VILLAGER_HAPPY);

        int plotSize = TowerCache.getTowerSize(bottomRightCorner, b -> b.getType() != type);
        if (plotSize < tower.getSize()) {
            return BuildResult.INVALID_SIZE;
        }
        if (plotSize == tower.getSize()) {
            location.set(bottomRightCorner.x(), bottomRightCorner.y(), bottomRightCorner.z());
            return BuildResult.SUCCESS;
        }
        if (canBeBuilt(tower, type, location, playerLocation)) {
            // align to bottom right corner relative to location
            return BuildResult.SUCCESS;
        }
        return BuildResult.INVALID_LOCATION;
    }

    private static void alignToBottomRightCorner(Tower tower, Material type, LocationBuilder bottomRightCorner) {
        // move backwards and to the right until not same type, max tower size times
        int move = tower.getSize();
        for (int i = 0; i < move; i++) {
            bottomRightCorner.backward(1);
            if (bottomRightCorner.getBlock().getType() != type) {
                bottomRightCorner.forward(1);
                break;
            }
        }
        for (int i = 0; i < move; i++) {
            bottomRightCorner.right(1);
            if (bottomRightCorner.getBlock().getType() != type) {
                bottomRightCorner.left(1);
                break;
            }
        }
    }

    private void debugParticle(Location location, Particle particle) {
        if (debug) {
            location.getWorld().spawnParticle(particle, location, 2);
        }
    }

    /**
     * @param tower          the tower to build
     * @param type           the type of block that can be built on
     * @param location       the location to build at
     * @param playerLocation the location of the player
     * @return true if the tower can be built at the location
     */
    private boolean canBeBuilt(Tower tower, Material type, Location location, @NotNull Location playerLocation) {
        int towerSize = tower.getSize();
        // first check if looking down / close to 90 degrees
        if (playerLocation.getPitch() > 70) {
            // check if surrounded by same type
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location locationToCheck = location.clone().add(x, 0, z);
                    if (locationToCheck.getBlock().getType() != type) {
                        debugParticle(locationToCheck.clone().add(.5, 1, .5), Particle.FLAME);
                        return false;
                    } else {
                        debugParticle(locationToCheck.clone().add(.5, 1, .5), Particle.VILLAGER_HAPPY);
                    }
                }
            }
            // align to bottom right corner relative to location
            location.setX(location.getX() - 1);
            location.setZ(location.getZ() - 1);
            return true;
        }
        BlockFace facing = locationToBlockFace(playerLocation);
        // check if can be built based on facing direction and tower size
        int modX = facing.getModX();
        int modZ = facing.getModZ();
        Integer lowestX = null; // bottom right corner
        Integer lowestZ = null; // bottom right corner
        for (int x = 0; x < towerSize; x++) {
            for (int z = 0; z < towerSize; z++) {
                Location locationToCheck = location.clone().add(x * modX, 0, z * modZ);
                if (locationToCheck.getBlock().getType() != type) {
                    debugParticle(locationToCheck.clone().add(.5, 1, .5), Particle.FLAME);
                    return false;
                } else {
                    debugParticle(locationToCheck.clone().add(.5, 1, .5), Particle.VILLAGER_HAPPY);
                }
                if (lowestX == null || locationToCheck.getX() < lowestX) {
                    lowestX = locationToCheck.getBlockX();
                }
                if (lowestZ == null || locationToCheck.getZ() < lowestZ) {
                    lowestZ = locationToCheck.getBlockZ();
                }
            }
        }
        if (lowestX == null) {
            return false;
        }
        // align to bottom right corner relative to location
        location.setX(lowestX);
        location.setZ(lowestZ);
        return true;
    }

    /**
     * @param location location to convert to block face
     * @return block face, NE, NW, SE, SW
     */
    public static BlockFace locationToBlockFace(Location location) {
        float yaw = location.getYaw();
        if (yaw > 0) {
            if (yaw <= 90) {
                return BlockFace.SOUTH_WEST;
            }
            return BlockFace.NORTH_WEST;
        }
        if (yaw >= -90) {
            return BlockFace.SOUTH_EAST;
        }
        return BlockFace.NORTH_EAST;
    }

    public List<Tower> getBuiltTowers() {
        return builtTowers;
    }

    public void toggleDebug() {
        debug = !debug;
    }

    public boolean isDebug() {
        return debug;
    }

    enum BuildResult {
        SUCCESS,
        INVALID_LOCATION,
        INVALID_SIZE
    }

}
