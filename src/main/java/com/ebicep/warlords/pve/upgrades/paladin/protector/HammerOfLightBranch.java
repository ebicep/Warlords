package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilties.HammerOfLight;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HammerOfLightBranch extends AbstractUpgradeBranch<HammerOfLight> {

    public HammerOfLightBranch(AbilityTree abilityTree, HammerOfLight ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Duration - Tier I", "+1s Duration", 5000));
        treeA.add(new Upgrade("Duration - Tier II", "+2s Duration", 10000));
        treeA.add(new Upgrade("Duration - Tier III", "+4s Duration", 20000));

        treeC.add(new Upgrade("Damage/Healing - Tier I", "+10% Damage and Healing", 5000));
        treeC.add(new Upgrade("Damage/Healing - Tier II", "+20% Damage and Healing", 10000));
        treeC.add(new Upgrade("Damage/Healing - Tier III", "+40% Damage and Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "For each ally within the Hammer of Light (including\nyourself), increase damage dealt by Protector's\nStrike by 10%.\n\nFor each ally within the Crown of Light radius,\nincrease the healing of the caster's abilities\nby 20% (maximum 4 allies.)",
                50000
        );
    }

    int duration = ability.getDuration();

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

}
