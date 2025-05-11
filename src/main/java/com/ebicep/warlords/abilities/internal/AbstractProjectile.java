package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public abstract class AbstractProjectile<T extends AbstractPiercingProjectile<T, R>, R extends AbstractPiercingProjectile.AbstractPiercingProjectileStats<T, R>> extends AbstractPiercingProjectile<T, R> {

    public AbstractProjectile(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return true;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        projectile.getHit().add(hit);
    }

}
