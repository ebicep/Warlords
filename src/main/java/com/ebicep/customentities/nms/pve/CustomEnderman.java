package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import javax.annotation.Nonnull;

public class CustomEnderman extends EnderMan implements CustomEntity<CustomEnderman> {

    public CustomEnderman(ServerLevel serverLevel) {
        super(EntityType.ENDERMAN, serverLevel);
        resetAI();
        giveBaseAI();
    }

    public CustomEnderman(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomEnderman get() {
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
        return DisguiseType.ENDERMAN;
    }
}
