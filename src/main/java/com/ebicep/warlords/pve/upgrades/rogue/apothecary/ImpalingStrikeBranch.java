package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.ImpalingStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ImpalingStrikeBranch extends AbstractUpgradeBranch<ImpalingStrike> {

    public ImpalingStrikeBranch(AbilityTree abilityTree, ImpalingStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+2.5% Leech healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+5% Leech healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+10% Leech healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Impaling Strike deals damage to 2 additional enemies and inflicts leech on those targets.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    float energyCost = ability.getEnergyCost();

    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

}
