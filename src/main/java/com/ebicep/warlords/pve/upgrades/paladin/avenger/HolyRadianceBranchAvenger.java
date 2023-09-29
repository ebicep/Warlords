package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.HolyRadianceAvenger;
import com.ebicep.warlords.pve.upgrades.*;

public class HolyRadianceBranchAvenger extends AbstractUpgradeBranch<HolyRadianceAvenger> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public HolyRadianceBranchAvenger(AbilityTree abilityTree, HolyRadianceAvenger ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability, .1f)
                .addUpgradeHitBox(ability, 1, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * value);
                        ability.setMaxDamageHeal(maxDamage * value);
                    }
                }, 10f)
                .addUpgradeHitBox(ability, 1, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Avenger's Mark is now AoE. Additionally, marked targets take 40% more damage from Avenger's Strike and receive strike priority.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Unrivalled Radiance",
                "Holy Radiance - Master Upgrade",
                """
                        Avenger's Mark is now an AoE. Additionally, marked targets that are defeated will now reduce the cooldown of Avenger's Wrath by .5s at a max reduction of 5s. Marked targets receive strike priority.
                        """,
                50000,
                () -> {

                }
        );
    }
}
