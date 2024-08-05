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
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBeam<T extends AbstractPiercingProjectile<T, R>, R extends AbstractPiercingProjectile.AbstractPiercingProjectileStats<T, R>> extends AbstractPiercingProjectile<T, R> implements RedAbilityIcon, AbilityStats<T, R> {

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


    public static abstract class AbstractBeamStats<T extends AbstractPiercingProjectile<T, R>, R extends AbstractPiercingProjectile.AbstractPiercingProjectileStats<T, R>> extends AbstractPiercingProjectileStats<T, R> {

        @Field("stacks_removed")
        private Map<Integer, Integer> stacksRemoved = new HashMap<>();

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>();
            statsDisplay.add(new AbilityStatDisplay("Times Used", timesUsed));
            stacksRemoved.forEach((key, value) -> statsDisplay.add(new AbilityStatDisplay("Stacks Removed (" + key + ")", value)));
            return statsDisplay;
        }

        public Map<Integer, Integer> getStacksRemoved() {
            return stacksRemoved;
        }

    }

}
