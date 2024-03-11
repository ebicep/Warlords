package com.ebicep.warlords.game.option.towerdefense.towers;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public enum TowerRegistry {

    PYRO_TOWER(PyromancerTower::new, Material.FIRE_CHARGE, "Pyromancer Tower", "Fire."),
    CRYO_TOWER(CryomancerTower::new, Material.SNOWBALL, "Cryomancer Tower", "Slow it down."),
    AQUA_TOWER(AquamancerTower::new, Material.WATER_BUCKET, "Aquamancer Tower", "Very wet."),
    AVENGER_TOWER(AvengerTower::new, Material.RED_CONCRETE_POWDER, "Avenger Tower", "Strike."),
    CRUSADER_TOWER(CrusaderTower::new, Material.YELLOW_CONCRETE_POWDER, "Crusader Tower", "Buffer."),
    PROTECTOR_TOWER(ProtectorTower::new, Material.GREEN_CONCRETE_POWDER, "Protector Tower", "Protects."),
    BERSERKER_TOWER(BerserkerTower::new, Material.RED_CONCRETE_POWDER, "Berserker Tower", "Grr."),
    BIG_BOY(BigBoy::new, Material.SLIME_BLOCK, "Big Boy Tower", "Big"),

    ;

    public static final TowerRegistry[] VALUES = values();
    @Nonnull
    private static final World TOWER_WORLD = Objects.requireNonNull(Bukkit.getWorld("Towers"));
    private static final int MAX_TOWER_SIZE = 10; // width and length
    private static final int MAX_TOWER_HEIGHT = 15; // height

    public static EnumSet<TowerRegistry> updateCaches() {
        EnumSet<TowerRegistry> updated = EnumSet.noneOf(TowerRegistry.class);
        List<TowerRegistry> values = new ArrayList<>(List.of(TowerRegistry.VALUES));
        for (int i = 0; i < 1000; i++) {
            Block block = TOWER_WORLD.getBlockAt(i, 100, 0);
            if (block.getType() == Material.RED_WOOL) {
                break;
            }
            if (!(block.getState() instanceof Sign sign)) {
                continue;
            }
            SignSide side = sign.getSide(Side.FRONT);
            if (!(side.line(0) instanceof TextComponent towerName) || !(side.line(1) instanceof TextComponent upgradeIndexText)) {
                continue;
            }
            Integer upgradeIndex = null;
            try {
                upgradeIndex = Integer.parseInt(upgradeIndexText.content());
            } catch (NumberFormatException ignored) {

            }
            for (TowerRegistry tower : values) {
                if (tower.name().equals(towerName.content())) {
                    updateTowerCache(sign.getLocation().add(1, 0, 1), tower, upgradeIndex);
                    updated.add(tower);
                    values.remove(tower);
                    break;
                }
            }
        }
        return updated;
    }

    private static void updateTowerCache(Location start, TowerRegistry tower, @Nullable Integer upgradeIndex) {
        start.setPitch(0);
        start.setYaw(0);
        int size = getTowerSize(start.clone().add(0, -1, 0));
        int height = getTowerHeight(start, size);

        BlockData[][][] blockData = new BlockData[size][size][height];
        if (upgradeIndex == null) {
            tower.baseTowerData = blockData;
        } else {
            tower.upgradeTowerData.put(upgradeIndex, blockData);
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < size; x++) {
                for (int z = 0; z < size; z++) {
                    blockData[x][z][y] = start.clone().add(x, y, z).getBlock().getBlockData();
                }
            }
        }
        String upgradeInfo = upgradeIndex == null ? "BASE " : "UPGRADE " + upgradeIndex + " ";
        ChatChannels.sendDebugMessage((CommandIssuer) null, ComponentBuilder.create(upgradeInfo + tower.name(), NamedTextColor.YELLOW)
                                                                            .text(" - ", NamedTextColor.GRAY)
                                                                            .text(size + "x" + size + "x" + height, NamedTextColor.GREEN)
                                                                            .text(" updated", NamedTextColor.GRAY)
                                                                            .build());
    }

    private static int getTowerSize(Location start) {
        return getTowerSize(start, block -> block.getType() == Material.WHITE_WOOL);
    }

    private static int getTowerHeight(Location start, int size) {
        LocationBuilder builder = new LocationBuilder(start);
        for (int i = 0; i < MAX_TOWER_HEIGHT; i++) {
            // iterate through size x size to check if all air
            boolean allAir = true;
            for (int j = 0; j < size * size; j++) {
                Block block = builder.clone().add(j % size, 0, j / size).getBlock();
                if (block.getType() != Material.AIR) {
                    allAir = false;
                    break;
                }
            }
            if (allAir) {
                return i;
            }
            builder.add(0, 1, 0);
        }
        return -1;
    }

    public static int getTowerSize(Location start, Predicate<Block> predicate) {
        int size = 0;
        LocationBuilder builder = new LocationBuilder(start);
        for (int i = 0; i < MAX_TOWER_SIZE; i++) {
            // check diagonal
            if (predicate.test(builder.getBlock())) {
                return i;
            }
            // check going right and down from diagonal
            for (int j = 0; j < i; j++) {
                Location right = builder.clone().add(-j, 0, 0);
                if (predicate.test(right.getBlock())) {
                    return i;
                }
                Location down = builder.clone().add(0, 0, -j);
                if (predicate.test(down.getBlock())) {
                    return i;
                }
            }
            builder.add(1, 0, 1);
        }
        return size;
    }

    public final TriFunction<Game, UUID, Location, AbstractTower> create;
    public final Material material;
    public String name;
    public String description;
    public int cost = 1000; // TODO
    public BlockData[][][] baseTowerData; // [x][z][y]
    public Map<Integer, BlockData[][][]> upgradeTowerData = new HashMap<>(); // data of tower at any upgrade level if applicable

    TowerRegistry(TriFunction<Game, UUID, Location, AbstractTower> create, Material material, String name, String description) {
        this.create = create;
        this.material = material;
        this.name = name;
        this.description = description;
    }

    public int getSize() {
        return baseTowerData.length;
    }

    public int getHeight() {
        return baseTowerData[0][0].length;
    }

}
