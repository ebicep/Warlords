package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomWitch extends Witch implements CustomEntity<CustomWitch> {

    public CustomWitch(ServerLevel serverLevel) {
        super(EntityType.WITCH, serverLevel);
        resetAI();
        aiWander(1);
        aiLookAtPlayer();
    }

    public CustomWitch(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomWitch get() {
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
        return DisguiseType.WITCH;
    }
}
