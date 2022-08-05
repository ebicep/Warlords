package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilties.ProtectorsStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ProtectorStrikeBranch extends AbstractUpgradeBranch<ProtectorsStrike> {

    public ProtectorStrikeBranch(AbilityTree abilityTree, ProtectorsStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+20% Healing conversion", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+40% Healing conversion", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+80% Healing conversion", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Protector's Strike hits 2 additional targets and\n heals 2 additional allies. Protector's Strike deals\n20% of it's damage as true damage.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    float energyCost = ability.getEnergyCost();

    int minConversion = ability.getMinConvert();
    int maxConversion = ability.getMaxConvert();

}
