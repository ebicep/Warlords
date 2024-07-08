package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;

public abstract class AbstractProjectile extends AbstractPiercingProjectile {

    public AbstractProjectile(
            String name,
            float cooldown,
            float energyCost,
            double projectileSpeed,
            double maxDistance,
            boolean hitTeammates
    ) {
        super(name, cooldown, energyCost, projectileSpeed, maxDistance, hitTeammates);
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
