package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

import javax.annotation.Nonnull;

public class CustomPigZombie extends ZombifiedPiglin implements CustomEntity<CustomPigZombie> {

    public CustomPigZombie(ServerLevel serverLevel) {
        super(EntityType.ZOMBIFIED_PIGLIN, serverLevel);
        resetAI();
        giveBaseAI();
    }

    public CustomPigZombie(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomPigZombie get() {
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
