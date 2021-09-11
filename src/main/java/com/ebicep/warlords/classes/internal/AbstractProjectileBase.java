package com.ebicep.warlords.classes.internal;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;

public abstract class AbstractProjectileBase extends AbstractPiercingProjectileBase {


    public AbstractProjectileBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, double projectileSpeed, double maxDistance, boolean hitTeammates) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, projectileSpeed, maxDistance, hitTeammates);
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, WarlordsPlayer wp) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(InternalProjectile projectile, WarlordsPlayer hit, Location impactLocation) {
        projectile.getHit().add(hit);
    }

}
