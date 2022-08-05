package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilties.InspiringPresence;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InspiringPresenceBranch extends AbstractUpgradeBranch<InspiringPresence> {

    public InspiringPresenceBranch(AbilityTree abilityTree, InspiringPresence ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Duration - Tier I", "+1s Duration", 5000));
        treeA.add(new Upgrade("Duration - Tier II", "+2s Duration", 10000));
        treeA.add(new Upgrade("Duration - Tier III", "+4s Duration", 20000));

        treeC.add(new Upgrade("Energy - Tier I", "+5 Energy per second", 5000));
        treeC.add(new Upgrade("Energy - Tier II", "+10 Energy per second", 10000));
        treeC.add(new Upgrade("Energy - Tier III", "+15 Energy per second", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "-30% Cooldown reduction\n\nReset the cooldown on all caster's abilities (other than Inspiring Presence.)",
                50000
        );
    }

    int duration = ability.getDuration();

    int energyPerSecond = ability.getEnergyPerSecond();

}
