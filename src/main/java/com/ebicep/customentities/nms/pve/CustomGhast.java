package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

import javax.annotation.Nonnull;

public class CustomGhast extends Ghast implements CustomEntity<CustomGhast> {

    public CustomGhast(ServerLevel serverLevel) {
        super(EntityType.GHAST, serverLevel);
    }

    public CustomGhast(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomGhast get() {
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
}
