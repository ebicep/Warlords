package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

import javax.annotation.Nonnull;

public class CustomSlime extends Slime implements CustomEntity<CustomSlime> {

    private float customJumpPower = .1f;

    public CustomSlime(ServerLevel serverLevel) {
        super(EntityType.SLIME, serverLevel);
        setSize(5, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        resetTargetAI();
        giveBaseAI();
    }

    public CustomSlime(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x, customJumpPower, vec3d.z);
        this.hasImpulse = true;
    }

    @Override
    public CustomSlime get() {
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
        return DisguiseType.SLIME;
    }

    public float getCustomJumpPower() {
        return customJumpPower;
    }

    public void setCustomJumpPower(float customJumpPower) {
        this.customJumpPower = customJumpPower;
    }
}
