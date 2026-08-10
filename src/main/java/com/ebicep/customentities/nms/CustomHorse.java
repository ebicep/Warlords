package com.ebicep.customentities.nms;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import javax.annotation.Nonnull;

public class CustomHorse extends Horse {

    public CustomHorse(Location location) {
        super(EntityType.HORSE, ((CraftWorld) location.getWorld()).getHandle());
        setPos(location.getX(), location.getY(), location.getZ());
        setRot(location.getYaw(), location.getPitch());
    }

    @Override
    protected void registerGoals() {

    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return InteractionResult.FAIL; // Prevent
    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

}
