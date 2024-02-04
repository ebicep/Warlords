package com.ebicep.warlords.game.option.towerdefense;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
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

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Cached tower builds stored in 3d arrays of BlockData copied from a location in a world
 */
public class TowerCache {

    @Nonnull
    private static final World TOWER_WORLD = Objects.requireNonNull(Bukkit.getWorld("Towers"));
    private static final int MAX_TOWER_SIZE = 10; // width and length
    private static final int MAX_TOWER_HEIGHT = 15; // height

    public static EnumSet<Tower> updateCaches() {
        EnumSet<Tower> updated = EnumSet.noneOf(Tower.class);
        for (int i = 0; i < 1000; i++) {
            Block block = TOWER_WORLD.getBlockAt(10 - i, 100, -4);
            if (block.getType() == Material.RED_WOOL) {
                break;
            }
            if (!(block.getState() instanceof Sign sign)) {
                continue;
            }
            Component firstLine = sign.getSide(Side.FRONT).line(0);
            if (!(firstLine instanceof TextComponent text)) {
                continue;
            }
            for (Tower value : Tower.VALUES) {
                if (value.name().equals(text.content())) {
                    updateTowerCache(sign.getLocation().add(1, 0, 1), value);
                    updated.add(value);
                }
            }
        }
        return updated;
    }

    private static void updateTowerCache(Location start, Tower tower) {
        start.setPitch(0);
        start.setYaw(0);
        int size = getTowerSize(start.clone().add(0, -1, 0));
        int height = getTowerHeight(start, size);

        tower.data = new BlockData[size][size][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < size; x++) {
                for (int z = 0; z < size; z++) {
                    tower.data[x][z][y] = start.clone().add(x, y, z).getBlock().getBlockData();
                }
            }
        }
        ChatChannels.sendDebugMessage((CommandIssuer) null, ComponentBuilder.create(tower.name(), NamedTextColor.YELLOW)
                                                                            .text(" - ", NamedTextColor.GRAY)
                                                                            .text(size + "x" + size + "x" + height, NamedTextColor.GREEN)
                                                                            .text(" updated", NamedTextColor.GRAY)
                                                                            .build());
    }

    private static int getTowerSize(Location start) {
        return getTowerSize(start, block -> block.getType() == Material.WHITE_WOOL);
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

    public enum Tower {
        PYRO_TOWER_1,
        BIG_BOY_1,

        ;
        public static final Tower[] VALUES = values();
        public BlockData[][][] data; // [x][z][y]


    }

}
