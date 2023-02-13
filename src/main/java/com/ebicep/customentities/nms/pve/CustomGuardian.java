package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

import javax.annotation.Nonnull;

public class CustomGuardian extends Guardian implements CustomEntity<CustomGuardian> {

    public CustomGuardian(ServerLevel serverLevel) {
        super(EntityType.GUARDIAN, serverLevel);
    }


    public CustomGuardian(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomGuardian get() {
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
