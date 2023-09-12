package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.crusader.ConsecrateBranchCrusader;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ConsecrateCrusader extends AbstractConsecrate {

    public ConsecrateCrusader() {
        super(144, 194.4f, 50, 15, 200, 15, 4);
    }

    public ConsecrateCrusader(
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
        return new ConsecrateBranchCrusader(abilityTree, this);
    }

    @Nonnull
    @Override
    public String getStrikeName() {
        return "Crusader's Strike";
    }

    @Nonnull
    @Override
    public AbstractConsecrate createConsecrate() {
        return new ConsecrateCrusader(minDamageHeal, maxDamageHeal, energyCost.getCurrentValue(), critChance, critMultiplier, strikeDamageBoost, radius, location);
    }
}
