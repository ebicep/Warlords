package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import javax.annotation.Nonnull;

public class CustomWolf extends Wolf implements CustomEntity<CustomWolf> {

    public CustomWolf(ServerLevel serverLevel) {
        super(EntityType.WOLF, serverLevel);
        resetAI();
        giveBaseAI();
    }

    public CustomWolf(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public CustomWolf get() {
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
