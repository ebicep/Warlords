package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.Vindicate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class VindicateBranch extends AbstractUpgradeBranch<Vindicate> {

    public VindicateBranch(AbilityTree abilityTree, Vindicate ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Duration - Tier I", "+2s Duration", 5000));
        treeA.add(new Upgrade("Duration - Tier II", "+4s Duration", 10000));
        treeA.add(new Upgrade("Duration - Tier III", "+8s Duration", 20000));

        treeC.add(new Upgrade("Damage Reduction - Tier I", "+10% Damage reduction", 5000));
        treeC.add(new Upgrade("Damage Reduction - Tier II", "+20% Damage reduction", 10000));
        treeC.add(new Upgrade("Damage Reduction - Tier III", "+40% Damage reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "-30% Cooldown reduction\n\nThe caster gains 25% speed and +10 energy\nper second for the duration of Vindicate",
                50000
        );
    }

    int duration = ability.getVindicateDuration();
    int resistDuration = ability.getVindicateSelfDuration();

    float damageReduction = ability.getVindicateDamageReduction();

}
