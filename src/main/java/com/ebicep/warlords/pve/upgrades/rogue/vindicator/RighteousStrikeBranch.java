package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.RighteousStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class RighteousStrikeBranch extends AbstractUpgradeBranch<RighteousStrike> {


    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public RighteousStrikeBranch(AbilityTree abilityTree, RighteousStrike ability) {
        super(abilityTree, ability);
        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Righteous Slash",
                "Righteous Strike - Master Upgrade",
                "Righteous Strike hits 4 additional enemies. Additionally, your initial strike target will be silenced for 6 seconds" +
                        " when struck, additional targets will be silenced for 4 seconds instead.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Righteous Assault",
                "Righteous Strike - Master Upgrade",
                """
                        Righteous Strike hits 4 additional enemies. Every enemy hit, refund yourself 2.5 energy. Every 5 enemies, will reduce the cooldown of Soul Shackle by 0.5 seconds.
                        """,
                50000,
                () -> {

                }
        );
    }
}
