package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.RighteousStrike;
import com.ebicep.warlords.pve.upgrades.*;

public class RighteousStrikeBranch extends AbstractUpgradeBranch<RighteousStrike> {

    float minDamage;
    float maxDamage;

    public RighteousStrikeBranch(AbilityTree abilityTree, RighteousStrike ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.35f);
            ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.35f);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
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
