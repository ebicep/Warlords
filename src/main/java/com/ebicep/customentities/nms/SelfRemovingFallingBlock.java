package com.ebicep.customentities.nms;

import com.ebicep.warlords.events.GeneralEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SelfRemovingFallingBlock extends FallingBlockEntity {

    private final double yLevelToRemove;

    public SelfRemovingFallingBlock(Location spawnLocation, BlockData blockData, double yDiff) {
        this(spawnLocation, blockData, yDiff, null);
    }

    public SelfRemovingFallingBlock(Location spawnLocation, BlockData blockData, double yDiff, @Nullable Consumer<FallingBlock> preSpawn) {
        this(
                ((CraftWorld) spawnLocation.getWorld()).getHandle(),
                spawnLocation.getX(),
                spawnLocation.getY(),
                spawnLocation.getZ(),
                ((CraftBlockData) blockData).getState(),
                yDiff,
                preSpawn
        );
    }

    public SelfRemovingFallingBlock(ServerLevel serverLevel, double x, double y, double z, BlockState blockState, double yDiff, @Nullable Consumer<FallingBlock> preSpawn) {
        super(serverLevel, x, y, z, blockState);
        this.yLevelToRemove = y - yDiff;

        CraftEntity bukkitEntity = this.getBukkitEntity();
        if (preSpawn != null) {
            preSpawn.accept((FallingBlock) bukkitEntity);
        }
        this.dropItem = false;

        GeneralEvents.addEntityUUID(bukkitEntity);

        // auto spawn
        serverLevel.addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getY() < yLevelToRemove) {
            this.discard();
        }
    }
}
