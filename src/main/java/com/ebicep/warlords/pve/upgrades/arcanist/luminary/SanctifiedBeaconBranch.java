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
                        Enemies within the radius have their movement speed reduced by 30%. Additionally, the beacon will grant Merciful Hex stacks twice as fast.
                        """,
                50000,
                () -> {
                    ability.setHexIntervalTicks((int) (ability.getHexIntervalTicks() * 0.5f));
                }
        );
        masterUpgrade2 = new Upgrade(
                "Shadow Garden",
                "Sanctified Beacon - Master Upgrade",
                """
                        Sanctified Beacon's radius is doubled. Additionally, allies within range will be healed 2.5% of their max hp every second for 10s.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addMultiplicativeModifierMult("Master Upgrade Branch", 2);
                }
        );
    }

}
