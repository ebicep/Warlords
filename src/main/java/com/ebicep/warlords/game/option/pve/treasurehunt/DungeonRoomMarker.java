package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.GameMarker;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Location;
import org.bukkit.World;

public interface DungeonRoomMarker extends GameMarker {

    Room getRoom();

    void renderInWorld(int x, int y, int z);

    static DungeonRoomMarker create(
            World world,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            RoomType type,
            boolean north,
            boolean east,
            boolean south,
            boolean west
    ) {
        var room = Room.makeSimpleRoom(maxX - minX + 1, maxZ - minZ + 1, type, north, east, south, west);
        return new DungeonRoomMarker() {
            @Override
            public Room getRoom() {
                return room;
            }

            @Override
            public void renderInWorld(int x, int y, int z) {
                CuboidRegion region = new CuboidRegion(
                        BukkitAdapter.asBlockVector(new Location(world, minX, minY, minZ)),
                        BukkitAdapter.asBlockVector(new Location(world, maxX, maxY, maxZ))
                );
                EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1);
                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                        BukkitAdapter.adapt(world),
                        region,
                        BukkitAdapter.asBlockVector(new Location(world, minX, minY, minZ)),
                        editSession,
                        BukkitAdapter.asBlockVector(new Location(world, x, y, z))
                );

                try {
                    Operations.complete(forwardExtentCopy);
                    editSession.close();
                } catch (WorldEditException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void clearInWorld(int x, int y, int z) {
                // destination region has same size as the template box
                var dx = maxX - minX;  // inclusive sizes -> use dx, dy, dz as offsets
                var dy = maxY - minY;
                var dz = maxZ - minZ;

                var weWorld = BukkitAdapter.adapt(world);
                try (EditSession edit = WorldEdit.getInstance()
                        .getEditSessionFactory()
                        .getEditSession(weWorld, -1)) {

                    var dstMin = BukkitAdapter.asBlockVector(new Location(world, x, y, z));
                    var dstMax = BukkitAdapter.asBlockVector(new Location(world, x + dx, y + dy, z + dz));
                    var target = new CuboidRegion(dstMin, dstMax);

                    edit.setBlocks(target, BlockTypes.AIR.getDefaultState());
                } catch (MaxChangedBlocksException e) {
                    throw new RuntimeException(e);
                }
            }

            static void clearArea(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
                var weWorld = BukkitAdapter.adapt(world);
                try (EditSession edit = WorldEdit.getInstance()
                        .getEditSessionFactory()
                        .getEditSession(weWorld, -1)) {

                    var region = new CuboidRegion(
                            BukkitAdapter.asBlockVector(new Location(world, minX, minY, minZ)),
                            BukkitAdapter.asBlockVector(new Location(world, maxX, maxY, maxZ))
                    );

                    edit.setBlocks(region, com.sk89q.worldedit.world.block.BlockTypes.AIR.getDefaultState());
                } catch (MaxChangedBlocksException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    default Option asOption() {
        return new MarkerOption(this);
    }

    void clearInWorld(int x, int y, int z);
}
