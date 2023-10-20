package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

import javax.annotation.Nonnull;

public class CustomIronGolem extends IronGolem implements CustomEntity<CustomIronGolem> {

    private boolean stunned;

    public CustomIronGolem(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }


    public CustomIronGolem(ServerLevel serverLevel) {
        super(EntityType.IRON_GOLEM, serverLevel);
        resetAI();
        giveBaseAI(1.0, 0.6, 100);
    }

    @Override
    public CustomIronGolem get() {
        return this;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    /**
     * On collide with entity, overrided so any entity that collides will this will not be targeted
     *
     * @param entity The entity to check against
     */
    @Override
    protected void doPush(@Nonnull Entity entity) {
    }

    @Override
    public boolean canCollideWithBukkit(@Nonnull Entity entity) {
        return !stunned;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public DisguiseType getDisguiseType() {
        return DisguiseType.IRON_GOLEM;
    }
}
