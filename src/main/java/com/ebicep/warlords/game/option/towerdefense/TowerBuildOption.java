package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.towerdefense.events.TowerSellEvent;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TowerBuildOption implements Option, Listener {

    private static final EnumSet<Material> BUILDABLE = EnumSet.of(Material.OAK_PLANKS, Material.DARK_OAK_PLANKS);
    private static final ItemStack BUILD_TOWER_ITEM = new ItemBuilder(Material.STICK)
            .name(Component.text("Build Tower", NamedTextColor.GREEN))
            .setOnUseID("BUILD_TOWER_ITEM")
            .setPlaceableOn(BUILDABLE)
            .get();

    private final Map<Team, List<Pair<Location, Location>>> teamBuildableAreas = new HashMap<>();
    private final Map<AbstractTower, Integer> builtTowers = new HashMap<>();
    private final Map<UUID, PlayerBuildData> playerBuildData = new HashMap<>();
    private Game game;
    private TowerDefenseOption towerDefenseOption;
    private boolean debug = false;

    public TowerBuildOption addBuildableArea(Team team, Location firstCorner, Location secondCorner) {
        teamBuildableAreas.computeIfAbsent(team, k -> new ArrayList<>()).add(new Pair<>(firstCorner, secondCorner));
        return this;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption defenseOption) {
                this.towerDefenseOption = defenseOption;
                break;
            }
        }
        game.registerEvents(this);
    }

    @Override
    public void start(@Nonnull Game game) {
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        builtTowers.keySet().forEach(AbstractTower::remove);
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        player.getInventory().setItem(6, BUILD_TOWER_ITEM);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (!TowerDefenseUtils.validInteractGame(game, warlordsEntity)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        boolean clickedTower = handleTowerClick(player, warlordsEntity, clickedBlock);
        if (clickedTower) {
            return;
        }
        if (!TowerDefenseUtils.validInteract(event, "BUILD_TOWER_ITEM")) {
            return;
        }
        event.setCancelled(true);

        if (!BUILDABLE.contains(clickedBlock.getType())) {
            return;
        }
        if (getPlayerBuildData(player).getCooldown() > System.currentTimeMillis()) {
            return;
        }
        Location clickedLocation = clickedBlock.getLocation();
        if (teamBuildableAreas.getOrDefault(warlordsEntity.getTeam(), new ArrayList<>())
                              .stream()
                              .noneMatch(pair -> TowerDefenseUtils.insideArea(clickedLocation, pair.getA(), pair.getB()))
        ) {
            player.sendMessage(Component.text("You can only build on your teams side!", NamedTextColor.RED));
            return;
        }
        if (player.isSneaking()) {
            // TODO check if tower is already there and auto upgrade etc
            if (false) {

            } else {
                TowerRegistry lastBuilt = getPlayerBuildData(player).getLastBuilt();
                if (lastBuilt != null) {
                    tryBuildTower(player, warlordsEntity, clickedLocation, lastBuilt);
                } else {
                    openBuildMenu(player, warlordsEntity, clickedLocation);
                }
            }
        } else {
            openBuildMenu(player, warlordsEntity, clickedLocation);
        }
    }

    @EventHandler
    public void onTowerSell(TowerSellEvent event) {
        AbstractTower tower = event.getTower();
        builtTowers.remove(tower);
        tower.remove();
    }

    private boolean handleTowerClick(Player player, WarlordsEntity warlordsEntity, Block clickedBlock) {
        for (MetadataValue metadataValue : clickedBlock.getMetadata("TOWER")) {
            if (metadataValue.value() instanceof AbstractTower tower) {
                TowerDefenseMenu.openTowerMenu(player, warlordsEntity, tower);
                return true;
            }
        }
        return false;
    }

    private PlayerBuildData getPlayerBuildData(Player player) {
        return getPlayerBuildData(player.getUniqueId());
    }

    private BuildResult buildTower(Player player, WarlordsEntity warlordsEntity, Location location, TowerRegistry tower) {
        if (tower.baseTowerData.length == 0) {
            return BuildResult.NO_TOWER_DATA;
        }
        if (warlordsEntity.getCurrency() < tower.cost) {
            return BuildResult.MISSING_CURRENCY;
        }
        Location alignedLocation = location.clone();
        BuildResult buildResult = getAlignedLocation(alignedLocation, player.getLocation(), tower);
        if (buildResult == BuildResult.SUCCESS) {
            alignedLocation.setYaw(0);
            // check if no other towers are in the way
            for (AbstractTower builtTower : builtTowers.keySet()) {
                Block[][][] builtBlocks = builtTower.getBlocks();
                Block builtTowerFirstCorner = builtTower.getBlocks()[0][0][0]; // front bottom right corner
                if (intersects(
                        alignedLocation.getX(), alignedLocation.getZ(),
                        alignedLocation.getX() + tower.getSize(), alignedLocation.getZ() + tower.getSize(),
                        builtTowerFirstCorner.getX(), builtTowerFirstCorner.getZ(),
                        builtTowerFirstCorner.getX() + builtBlocks.length, builtTowerFirstCorner.getZ() + builtBlocks[0].length
                )) {
                    return BuildResult.INTERSECTS;
                }
            }
            builtTowers.put(tower.create.apply(game, player.getUniqueId(), alignedLocation.add(0, 1, 0)), towerDefenseOption.getTicksElapsed());
            getPlayerBuildData(player).setLastBuilt(tower);
        }
        return buildResult;
    }

    private PlayerBuildData getPlayerBuildData(UUID uuid) {
        return playerBuildData.computeIfAbsent(uuid, k -> new PlayerBuildData());
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
    private BuildResult getAlignedLocation(Location location, @NotNull Location playerLocation, TowerRegistry tower) {
        Block block = location.getBlock();
        Material type = block.getType();
        location.setPitch(0);

        LocationBuilder bottomRightCorner = new LocationBuilder(location).yaw(0); // facing south
        TowerDefenseUtils.alignToBottomRightCorner(tower, type, bottomRightCorner);

        debugParticle(location.clone().add(.5, 1, .5), Particle.VILLAGER_HAPPY);
        debugParticle(bottomRightCorner.clone().add(.5, 1, .5), Particle.VILLAGER_HAPPY);

        int plotSize = TowerRegistry.getTowerSize(bottomRightCorner, b -> b.getType() != type);
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

    private boolean intersects(
            double tower1minX, double tower1minZ,
            double tower1maxX, double tower1maxZ,
            double tower2minX, double tower2minZ,
            double tower2maxX, double tower2maxZ
    ) {
        return tower1minX < tower2maxX && tower1maxX > tower2minX && tower1minZ < tower2maxZ && tower1maxZ > tower2minZ;
    }

    private void debugParticle(Location location, Particle particle) {
        if (debug) {
            EffectUtils.displayParticle(particle, location, 2);
        }
    }

    /**
     * @param tower          the tower to build
     * @param type           the type of block that can be built on
     * @param location       the location to build at
     * @param playerLocation the location of the player
     * @return true if the tower can be built at the location
     */
    private boolean canBeBuilt(TowerRegistry tower, Material type, Location location, @NotNull Location playerLocation) {
        int towerSize = tower.getSize();
        // first check if looking down / close to 90 degrees
        if (playerLocation.getPitch() > 55) {
            // check if surrounded by same type
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location locationToCheck = location.clone().add(x, 0, z);
                    if (locationToCheck.getBlock().getType() != type) {
                        debugParticle(locationToCheck.clone().add(.5, 2, .5), Particle.FLAME);
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
        boolean isCornerBlock = isCornerBlock(location.getBlock());
        BlockFace facing = TowerDefenseUtils.locationToBlockFace(playerLocation, false);
        LocationBuilder locationBuilder = new LocationBuilder(location);
        // check if can be built based on facing direction and tower size
        int modX = facing.getModX();
        int modZ = facing.getModZ();
        debugParticle(locationBuilder.clone().add(.5, 1, .5), Particle.ELECTRIC_SPARK);
        if (modX == 0) {
            modX = 1;
            if (!isCornerBlock) { // check corner to allow building straight, the build wont start at a corner
                locationBuilder.yaw((float) (Math.round(playerLocation.getYaw() / 90) * 90));
                locationBuilder.right(modZ);
            }
        }
        if (modZ == 0) {
            modZ = 1;
            if (!isCornerBlock) {
                locationBuilder.yaw((float) (Math.round(playerLocation.getYaw() / 90) * 90));
                locationBuilder.left(modX);
            }
        }
        Integer lowestX = null; // bottom right corner
        Integer lowestZ = null; // bottom right corner
        debugParticle(locationBuilder.clone().add(.5, 1, .5), Particle.HEART);
        for (int x = 0; x < towerSize; x++) {
            for (int z = 0; z < towerSize; z++) {
                Location locationToCheck = locationBuilder.clone().add(x * modX, 0, z * modZ);
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

    private boolean isCornerBlock(Block block) {
        int matching = 0;
        Material type = block.getType();
        if (block.getRelative(BlockFace.NORTH).getType() == type) {
            matching++;
        }
        if (block.getRelative(BlockFace.EAST).getType() == type) {
            matching++;
        }
        if (block.getRelative(BlockFace.SOUTH).getType() == type) {
            matching++;
        }
        if (block.getRelative(BlockFace.WEST).getType() == type) {
            matching++;
        }
        return matching == 2;
    }

    private void tryBuildTower(Player player, WarlordsEntity warlordsEntity, Location location, @Nonnull TowerRegistry tower) {
        BuildResult buildResult = buildTower(player, warlordsEntity, location, tower);
        player.sendMessage(Component.text("Build Result: " + buildResult.name(), NamedTextColor.GREEN));
        if (buildResult == BuildResult.SUCCESS) {
            PlayerBuildData buildData = getPlayerBuildData(player);
            buildData.setCooldown(System.currentTimeMillis() + 200);
            buildData.setLastBuilt(tower);
        }
    }

    private void openBuildMenu(Player player, WarlordsEntity warlordsEntity, Location clickedLocation) {
        Menu menu = new Menu("Build Tower", 9 * 4);
        TowerRegistry[] values = TowerRegistry.values();
        for (int i = 0; i < values.length; i++) {
            TowerRegistry tower = values[i];
            int size = tower.getSize();
            menu.setItem(i + 1, 1,
                    new ItemBuilder(tower.material)
                            .name(Component.text(tower.name, NamedTextColor.GREEN))
                            .lore(
                                    Component.text(tower.description),
                                    Component.empty(),
                                    ComponentBuilder.create("Size: ", NamedTextColor.GRAY).text(size + "x" + size, NamedTextColor.GREEN).build(),
                                    Component.empty(),
                                    ComponentBuilder.create("Cost: ").text(NumberFormat.addCommaAndRound(tower.cost) + " ❂ Insignia", NamedTextColor.GOLD).build(),
                                    Component.empty(),
                                    ComponentBuilder.create("Click to Build", NamedTextColor.YELLOW).build()
                            ) // TODO more info idk
                            .get(),
                    (m, e) -> {
                        tryBuildTower(player, warlordsEntity, clickedLocation, tower);
                        player.closeInventory();
                    }
            );
        }
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public Map<AbstractTower, Integer> getBuiltTowers() {
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
        INVALID_SIZE,
        INTERSECTS,
        MISSING_CURRENCY,
        NO_TOWER_DATA,
    }

    static final class PlayerBuildData {
        private long cooldown;
        @Nullable
        private TowerRegistry lastBuilt;

        public PlayerBuildData() {
            this(0, null);
        }

        PlayerBuildData(long cooldown, @Nullable TowerRegistry lastBuilt) {
            this.cooldown = cooldown;
            this.lastBuilt = lastBuilt;
        }

        public long getCooldown() {
            return cooldown;
        }

        public void setCooldown(long cooldown) {
            this.cooldown = cooldown;
        }

        @Nullable
        public TowerRegistry getLastBuilt() {
            return lastBuilt;
        }

        public void setLastBuilt(@Nullable TowerRegistry lastBuilt) {
            this.lastBuilt = lastBuilt;
        }
    }

}
