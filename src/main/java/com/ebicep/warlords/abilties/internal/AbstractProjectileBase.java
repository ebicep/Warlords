package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;
import org.bukkit.block.Block;

public abstract class AbstractProjectileBase extends AbstractPiercingProjectileBase {

    public AbstractProjectileBase(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            double projectileSpeed,
            double maxDistance,
            boolean hitTeammates
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, projectileSpeed, maxDistance, hitTeammates);
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, WarlordsEntity wp) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(InternalProjectile projectile, WarlordsEntity hit, Location impactLocation) {
        projectile.getHit().add(hit);
    }

}
