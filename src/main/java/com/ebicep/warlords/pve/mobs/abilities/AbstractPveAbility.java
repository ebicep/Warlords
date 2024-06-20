package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Easy to use abstract class for basic pve abilities
 */
public abstract class AbstractPveAbility extends AbstractAbility implements PvEAbility {

    protected PveOption pveOption;

    public AbstractPveAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost) {
        super(name, cooldown, energyCost);
    }

    public AbstractPveAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, boolean startNoCooldown) {
        super(name, cooldown, energyCost, startNoCooldown);
    }

    public AbstractPveAbility(String name, float cooldown, float energyCost) {
        super(name, cooldown, energyCost);
    }

    public AbstractPveAbility(String name, float cooldown, float energyCost, boolean startNoCooldown) {
        super(name, cooldown, energyCost, startNoCooldown);
    }

    public AbstractPveAbility(String name, float cooldown, float energyCost, float startCooldown) {
        super(name, cooldown, energyCost, startCooldown);
    }

    public AbstractPveAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super(name, cooldown, energyCost);
    }

    public AbstractPveAbility(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            float startCooldown
    ) {
        super(name, cooldown, energyCost, startCooldown);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        PveOption option = getPveOption(wp);
        if (option == null) {
            return false;
        }
        return onPveActivate(wp, option);
    }

    public abstract boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption);

    @Nullable
    @Override
    public PveOption getPveOption() {
        return pveOption;
    }

    @Override
    public void setPveOption(PveOption pveOption) {
        this.pveOption = pveOption;
    }
}
