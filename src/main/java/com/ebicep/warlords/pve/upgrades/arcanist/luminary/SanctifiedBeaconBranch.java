package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.SanctifiedBeacon;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class SanctifiedBeaconBranch extends AbstractUpgradeBranch<SanctifiedBeacon> {

    public SanctifiedBeaconBranch(AbilityTree abilityTree, SanctifiedBeacon ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, .5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Beacon of Gloom",
                "Sanctified Beacon - Master Upgrade",
                """
                        Sanctified Beacon's radius is doubled. Enemies within the radius have their movement speed reduced by 20%. Additionally, the beacon will grant Merciful Hex stacks twice as fast.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addMultiplicativeModifierMult("Master Upgrade Branch", 2);
                    ability.setHexIntervalTicks((int) (ability.getHexIntervalTicks() * 0.5f));
                    ability.setMaxBeaconsAtATime(2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Shadow Garden",
                "Sanctified Beacon - Master Upgrade",
                """
                        Sanctified Beacon's radius is doubled. Additionally, allies will have their crit multiplier increased by 25% and knockback resistance by 15%.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addMultiplicativeModifierMult("Master Upgrade Branch", 2);
                }
        );
    }

}
