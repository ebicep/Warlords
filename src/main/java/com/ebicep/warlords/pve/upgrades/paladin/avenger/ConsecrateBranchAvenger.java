package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.ConsecrateAvenger;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.paladin.AbstractConsecrateBranch;

public class ConsecrateBranchAvenger extends AbstractConsecrateBranch<ConsecrateAvenger> {

    public ConsecrateBranchAvenger(AbilityTree abilityTree, ConsecrateAvenger ability) {
        super(abilityTree, ability);

        masterUpgrade = new Upgrade(
                "Sanctify",
                "Consecrate - Master Upgrade",
                """
                        +2s duration.
                        
                        Consecrate damage occurs every 0.5s. Each damage pulse increases range by 0.2 blocks (max 20) and increases Consecrate's crit chance and crit multiplier by 2%.
                        
                        For each enemy inside Consecrate, increase your movement speed by 1.5% and your strike damage by 3%, each capped at 30%.
                        """,
                50000,
                () -> ability.setTickDuration(ability.getTickDuration() + 40)
        );

        masterUpgrade2 = new Upgrade(
                "Sanctifying Ring",
                "Consecrate - Master Upgrade",
                """
                        Transform consecrate into a mobile ring that travels with you, damaging nearby enemies every 1.5s.
                        Enemies hit by Sanctifying Ring take 30% more damage from your attacks while the ring is active.
                        """,
                50000,
                () -> {
                    ability.setStrikeDamageBoost(30);
                }
        );
    }

}
