package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractBeam extends AbstractPiercingProjectile implements RedAbilityIcon {

    public AbstractBeam(
            String name,
            float cooldown,
            float energyCost,
            double projectileSpeed,
            double maxDistance,
            boolean hitTeammates
    ) {
        super(name, cooldown, energyCost, projectileSpeed, maxDistance, hitTeammates);
        this.maxTicks = 0;
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .6f);
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
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation).backward(.5f);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter) {
        List<Location> locationsToFireShots = getLocationsToFireShots(shooter.getEyeLocation());
        for (Location locationsToFireShot : locationsToFireShots) {
            Location location = Utils.getTargetLocation(locationsToFireShot, (int) maxDistance).clone().add(.5, -1, .5).clone();
            EffectUtils.playChainAnimation(shooter.getLocation(), location, getBeamItem(), 9);
        }
        return super.onActivate(shooter);
    }

    public abstract ItemStack getBeamItem();
}
