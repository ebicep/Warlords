package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomMagmaCube extends MagmaCube implements CustomEntity<CustomMagmaCube> {

    private final int flameHitbox = 6;

    public CustomMagmaCube(ServerLevel serverLevel) {
        this(serverLevel, 7);
    }

    public CustomMagmaCube(ServerLevel serverLevel, int size) {
        super(EntityType.MAGMA_CUBE, serverLevel);
        setSize(size, true);
    }

    public CustomMagmaCube(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
        giveBaseAI(1.0, 0.6, 100);
    }

    public CustomMagmaCube(org.bukkit.World world, int size) {
        this(((CraftWorld) world).getHandle(), size);
        giveBaseAI(1.0, 0.6, 100);
    }

    //jump
    @Override
    protected void jumpFromGround() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x, 0.07F + (float) this.getSize() * 0.07F, vec3d.z);
        this.hasImpulse = true;
    }

    @Override
    protected void jumpInLiquid(@Nonnull TagKey<Fluid> fluid) {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x, 0.07F + (float) this.getSize() * 0.07F, vec3d.z);
        this.hasImpulse = true;
    }

    @Override
    public CustomMagmaCube get() {
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
        return DisguiseType.MAGMA_CUBE;
    }
}
