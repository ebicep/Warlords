package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomSlime extends Slime implements CustomEntity<CustomSlime> {

    public CustomSlime(ServerLevel serverLevel) {
        super(EntityType.SLIME, serverLevel);
        setSize(5, true);
        giveBaseAI();
    }

    public CustomSlime(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3d = this.getDeltaMovement();
        this.setDeltaMovement(vec3d.x, .1, vec3d.z);
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
    public DisguiseType getDisguiseType() {
        return DisguiseType.SLIME;
    }
}
