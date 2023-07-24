package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomCreeper extends Creeper implements CustomEntity<CustomCreeper> {

    public CustomCreeper(ServerLevel serverLevel) {
        super(EntityType.CREEPER, serverLevel);
    }

    public CustomCreeper(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomCreeper get() {
        return this;
    }

    private boolean stunned;

    @Override
    public boolean canCollideWithBukkit(@Nonnull Entity entity) {
        return !stunned;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public DisguiseType getDisguiseType() {
        return DisguiseType.CREEPER;
    }
}
