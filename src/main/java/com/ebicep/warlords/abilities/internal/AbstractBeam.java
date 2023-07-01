package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class AbstractBeam extends AbstractPiercingProjectile {

    public AbstractBeam(
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
        this.maxTicks = 0;
        this.playerHitbox += .25;
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        return 0;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return false;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter, @Nonnull Player player) {
        Location location = Utils.getTargetLocation(player, (int) maxDistance).clone().add(.5, .85, .5).clone();
        AbstractChain.spawnChain(shooter.getLocation(), location, getBeamItem());
        return super.onActivate(shooter, player);
    }

    public abstract ItemStack getBeamItem();
}
