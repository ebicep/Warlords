package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Giant;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

import javax.annotation.Nonnull;

public class CustomGiant extends Giant implements CustomEntity<CustomGiant> {

    public CustomGiant(ServerLevel serverLevel) {
        super(EntityType.GIANT, serverLevel);
    }

    public CustomGiant(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomGiant get() {
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
        return DisguiseType.GIANT;
    }
}
