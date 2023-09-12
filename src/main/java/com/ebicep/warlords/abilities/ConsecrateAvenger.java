package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.ConsecrateBranchAvenger;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ConsecrateAvenger extends AbstractConsecrate {

    public ConsecrateAvenger() {
        super(158.4f, 213.6f, 50, 20, 175, 20, 5);
    }

    public ConsecrateAvenger(
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
        return new ConsecrateBranchAvenger(abilityTree, this);
    }

    @Nonnull
    @Override
    public String getStrikeName() {
        return "Avenger's Strike";
    }

    @Nonnull
    @Override
    public AbstractConsecrate createConsecrate() {
        return new ConsecrateAvenger(minDamageHeal, maxDamageHeal, energyCost.getCurrentValue(), critChance, critMultiplier, strikeDamageBoost, radius, location);
    }
}
