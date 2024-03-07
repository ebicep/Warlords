package com.ebicep.warlords.pve.upgrades.paladin;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public abstract class AbstractConsecrateBranch<T extends AbstractConsecrate> extends AbstractUpgradeBranch<T> {

    public AbstractConsecrateBranch(AbilityTree abilityTree, T ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability, 10f)
                .addUpgradeCooldown(ability, 0.15f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 0.25f)
                .addUpgradeDuration(ability, 20f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Sanctify",
                "Consecrate - Master Upgrade",
                "-30 Energy cost\n+2 Additional blocks hit radius\n+20% Cooldown reduction",
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -30);
                    ability.getHitBoxRadius().addAdditiveModifier("Master Upgrade Branch", 2);
                    ability.getCooldown().addMultiplicativeModifierMult("Sanctify", 0.8f);
                }
        );
    }
}
