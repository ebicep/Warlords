package com.ebicep.customentities.nms;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Player-controlled ravager. No raid AI, roar, or block breaking.
 */
public class CustomRavager extends Ravager {

    public CustomRavager(Location location) {
        super(EntityType.RAVAGER, ((CraftWorld) location.getWorld()).getHandle());
        MountEntitySetup.prepare(this, location);
        var attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            attack.setBaseValue(0);
        }
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
    public int getAmbientSoundInterval() {
        return Integer.MAX_VALUE;
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return InteractionResult.FAIL;
    }

    @Override
    public boolean doHurtTarget(@Nonnull ServerLevel level, @Nonnull Entity target) {
        return false;
    }

    @Override
    protected void blockedByItem(@Nonnull LivingEntity entity) {
    }

    /**
     * Vanilla ravagers are not rideable on the client, so the server has to move this mob itself.
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

    @Override
    public void aiStep() {
        super.aiStep();
        // Ravager.aiStep lerps movement speed toward its own base and breaks leaves on collision.
        var movement = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movement != null) {
            movement.setBaseValue(MountEntitySetup.MOVEMENT_SPEED);
        }
    }

    @Override
    public void move(@Nonnull MoverType type, @Nonnull Vec3 movement) {
        super.move(type, movement);
        // Cleared after collision resolution so Ravager.aiStep does not grief or hop.
        this.horizontalCollision = false;
    }

}
