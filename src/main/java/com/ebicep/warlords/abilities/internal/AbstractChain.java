package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class AbstractChain extends AbstractAbility {

    public int playersHit = 0;
    protected int radius;
    protected int bounceRange;
    protected int additionalBounces;

    public AbstractChain(
            String name,
            float cooldown,
            float energyCost,
            int radius,
            int bounceRange,
            int additionalBounces
    ) {
        this(name, cooldown, energyCost, radius, bounceRange, additionalBounces, 0);
    }

    public AbstractChain(
            String name,
            float cooldown,
            float energyCost,
            int radius,
            int bounceRange,
            int additionalBounces,
            float startCooldown
    ) {
        super(name, cooldown, energyCost, startCooldown);
        this.radius = radius;
        this.bounceRange = bounceRange;
        this.additionalBounces = additionalBounces;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity warlordsPlayer) {
        Set<WarlordsEntity> entitiesHit = getEntitiesHitAndActivate(warlordsPlayer);
        int hitCounter = entitiesHit.size();
        if (hitCounter != 0) {
            playersHit += hitCounter;

            AbstractPlayerClass.sendRightClickPacket(warlordsPlayer);

            onHit(warlordsPlayer, hitCounter);

            entitiesHit.remove(null);

            Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent(warlordsPlayer, name, entitiesHit));

            return true;
        }

        return false;
    }

    protected abstract Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity warlordsPlayer);

    protected abstract void onHit(WarlordsEntity warlordsPlayer, int hitCounter);

    protected void chain(Location from, Location to) {
        EffectUtils.playChainAnimation(from, to, getChainItem(), 9);
    }

    protected abstract ItemStack getChainItem();

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBounceRange() {
        return bounceRange;
    }

    public void setBounceRange(int bounceRange) {
        this.bounceRange = bounceRange;
    }


    public int getAdditionalBounces() {
        return additionalBounces;
    }

    public void setAdditionalBounces(int additionalBounces) {
        this.additionalBounces = additionalBounces;
    }


}
