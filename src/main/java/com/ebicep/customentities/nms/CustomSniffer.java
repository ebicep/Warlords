package com.ebicep.customentities.nms;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Player-controlled sniffer. The brain never ticks, so it does not sniff or dig.
 */
public class CustomSniffer extends Sniffer {

    public CustomSniffer(Location location) {
        super(EntityType.SNIFFER, ((CraftWorld) location.getWorld()).getHandle());
        MountEntitySetup.prepare(this, location);
        setBaby(false);
        transitionTo(State.IDLING);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected void customServerAiStep(@Nonnull ServerLevel level) {
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public boolean isFood(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMate(@Nonnull Animal other) {
        return false;
    }

    @Override
    public boolean canDig() {
        return false;
    }

    @Override
    public int getAmbientSoundInterval() {
        return Integer.MAX_VALUE;
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    /**
     * Sniffers are not rideable on the client, so the server moves this mob itself.
     */
    @Override
    public boolean isClientAuthoritative() {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return MountEntitySetup.controllingPassenger(this);
    }

    @Override
    protected void tickRidden(@Nonnull Player player, @Nonnull Vec3 movementInput) {
        MountEntitySetup.tickRidden(this, player);
    }

    @Nonnull
    @Override
    protected Vec3 getRiddenInput(@Nonnull Player player, @Nonnull Vec3 movementInput) {
        return MountEntitySetup.riddenInput(player);
    }

    @Override
    protected float getRiddenSpeed(@Nonnull Player player) {
        return MountEntitySetup.riddenSpeed(this);
    }

}
