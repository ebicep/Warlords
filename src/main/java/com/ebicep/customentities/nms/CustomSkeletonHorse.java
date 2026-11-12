package com.ebicep.customentities.nms;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import javax.annotation.Nonnull;

public class CustomSkeletonHorse extends SkeletonHorse {

    public CustomSkeletonHorse(Location location) {
        super(EntityType.SKELETON_HORSE, ((CraftWorld) location.getWorld()).getHandle());
        MountEntitySetup.prepare(this, location);
        setTrap(false);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        clearFire();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    protected void addBehaviourGoals() {
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

}
