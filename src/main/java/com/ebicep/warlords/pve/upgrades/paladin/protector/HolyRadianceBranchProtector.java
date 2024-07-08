package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.HolyRadianceProtector;
import com.ebicep.warlords.pve.upgrades.*;

public class HolyRadianceBranchProtector extends AbstractUpgradeBranch<HolyRadianceProtector> {

    float markHealing = ability.getMarkBonusHealing();

    public HolyRadianceBranchProtector(AbilityTree abilityTree, HolyRadianceProtector ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getRadianceHealing(), 10f)
                .addUpgradeEnergy(ability, 30f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Mark Healing Bonus";
                    }

                    @Override
                    public void run(float value) {
                        ability.setMarkBonusHealing(markHealing + value);
                    }
                }, 5f)
                .addUpgradeCooldown(ability, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Protector's Mark is now AoE with a 15 block radius and has no target limit. Additionally, the mark duration is reduced by 3 seconds.",
                50000,
                () -> {
                    ability.setMarkDuration((int) (ability.getMarkDuration() * 0.5f));
                }
        );
        masterUpgrade2 = new Upgrade(
                "Unrivalled Radiance",
                "Holy Radiance - Master Upgrade",
                """
                        +12 Block Radius
                                                
                        Marked players will now emit healing to any player within 10 blocks for 150-350 health every 1s for 3s.
                        """,
                50000,
                () -> {
                    ability.getMarkRadius().addAdditiveModifier("Master Upgrade Branch", 12);
                }
        );
    }
}
