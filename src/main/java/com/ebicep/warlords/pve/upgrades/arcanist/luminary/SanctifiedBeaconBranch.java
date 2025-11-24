package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.SanctifiedBeacon;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

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
                "Beacon of Nine",
                "Sanctified Beacon - Master Upgrade",
                """
                        Sanctified Beacon's radius is doubled.
                        
                        The beacon alternates between sending out 3 seeking projectiles that heal allies for 1000 health,
                        or damage enemies for twice the amount. Additionally, the beacon will grant Merciful Hex stacks twice as fast.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Master Upgrade Branch", 2);
                    ability.setHexIntervalTicks((int) (ability.getHexIntervalTicks() * 0.5f));
                }
        );
        masterUpgrade2 = new Upgrade(
                "Shadow Garden",
                "Sanctified Beacon - Master Upgrade",
                """
                        Sanctified Beacon's radius is doubled.
                        
                        Additionally, allies within the radius will have their crit multiplier increased by 30% and knockback resistance by 50%.
                        
                        Enemies (excluding bosses) that walk through the beacon radius have their damage and movement speed permanently reduced by 30%.
                        """,
                50000,
                () -> {
                    ability.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Master Upgrade Branch", 2);
                }
        );
    }

}
