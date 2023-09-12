package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.ConsecrateBranchProtector;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ConsecrateProtector extends AbstractConsecrate {

    public ConsecrateProtector() {
        super(96, 130, 10, 15, 200, 15, 4);
    }

    public ConsecrateProtector(
            float minDamageHeal,
            float maxDamageHeal,
            float energyCost,
            float critChance,
            float critMultiplier,
            int strikeDamageBoost,
            float radius,
            Location location
    ) {
        super(minDamageHeal, maxDamageHeal, energyCost, critChance, critMultiplier, strikeDamageBoost, radius, location);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ConsecrateBranchProtector(abilityTree, this);
    }

    @Nonnull
    @Override
    public String getStrikeName() {
        return "Protector's Strike";
    }

    @Nonnull
    @Override
    public AbstractConsecrate createConsecrate() {
        return new ConsecrateProtector(minDamageHeal, maxDamageHeal, energyCost.getCurrentValue(), critChance, critMultiplier, strikeDamageBoost, radius, location);
    }
}
