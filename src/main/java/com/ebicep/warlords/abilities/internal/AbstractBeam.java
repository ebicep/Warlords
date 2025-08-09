package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
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

    public AbstractBeam(AbstractAbilityBuilder builder) {
        super(builder);
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .6f);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        List<Location> locationsToFireShots = getLocationsToFireShots(shooter.getEyeLocation());
        for (Location locationsToFireShot : locationsToFireShots) {
            int distance = (int) maxDistance.getCalculatedValue();
            Location location = Utils.getTargetLocation(locationsToFireShot, distance).clone().add(.5, .5, .5).clone();
            Pair<Float, Float> animationData = getChainAnimationData(distance);
            EffectUtils.playChainAnimation(shooter.getGame(), shooter.getEyeLocation(), location, getBeamItem(), animationData.getA(), animationData.getB(), 50);
        }
        return super.onActivateInternal(shooter);
    }

    public abstract Pair<Float, Float> getChainAnimationData(int distance);

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

    public abstract ItemStack getBeamItem();

    public static abstract class AbstractBeamStats<T extends AbstractPiercingProjectile<T, R>, R extends AbstractBeamStats<T, R>> extends AbstractPiercingProjectileStats<T, R> {

        @Field("stacks_removed")
        protected Map<Integer, Integer> stacksRemoved = new HashMap<>();

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>();
            statsDisplay.add(new AbilityStatDisplay("Times Used", timesUsed));
            stacksRemoved.forEach((key, value) -> statsDisplay.add(new AbilityStatDisplay("Stacks Removed (" + key + ")", value)));
            return statsDisplay;
        }

        @Override
        public R merge(R other, int multiplier) {
            R stats = super.merge(other, multiplier);
            this.stacksRemoved.forEach((key, value) -> stats.stacksRemoved.merge(key, value * multiplier, Integer::sum));
            other.stacksRemoved.forEach((key, value) -> stats.stacksRemoved.merge(key, value * multiplier, Integer::sum));
            return stats;
        }

        public Map<Integer, Integer> getStacksRemoved() {
            return stacksRemoved;
        }

    }

}
