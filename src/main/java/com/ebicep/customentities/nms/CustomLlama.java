package com.ebicep.customentities.nms;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Player-controlled llama. Rideable without a saddle, and it does not spit or join a caravan.
 */
public class CustomLlama extends Llama {

    public CustomLlama(Location location) {
        super(EntityType.LLAMA, ((CraftWorld) location.getWorld()).getHandle());
        MountEntitySetup.prepare(this, location);
        setBaby(false);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public boolean canEatGrass() {
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
    public int getAmbientSoundInterval() {
        return Integer.MAX_VALUE;
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

    @Override
    public void jumpFromGround() {
    }

    @Override
    public void standIfPossible() {
    }

    @Override
    public void handleStartJump(int jumpPower) {
    }

    @Override
    public void setStanding(int ticks) {
    }

    @Override
    protected void executeRidersJump(float strength, @Nonnull Vec3 movementInput) {
    }

    @Override
    public void onPlayerJump(int jumpPower) {
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public void performRangedAttack(@Nonnull LivingEntity target, float velocity) {
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return MountEntitySetup.controllingPassenger(this);
    }

}
