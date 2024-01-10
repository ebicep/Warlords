package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.pve.upgrades.*;

public class WoundingStrikeBranchBerserker extends AbstractUpgradeBranch<WoundingStrikeBerserker> {

    float minDamage;
    float maxDamage;

    @Override
    public void runOnce() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);

        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();
    }

    public WoundingStrikeBranchBerserker(AbilityTree abilityTree, WoundingStrikeBerserker ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeDuration(ability::setWoundingTickDuration, ability::getWoundingTickDuration, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lacerating Strike",
                "Wounding Strike - Master Upgrade",
                """
                        Wounding Strike now applies BLEED instead of wounding.

                        BLEED: Enemies afflicted take 100% more damage from Wounding Strike while Blood Lust is active. Bleeding enemies have healing reduced by 80% and lose 0.5% of their max health per second.""",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Lustful Strike",
                "Wounding Strike - Master Upgrade",
                """
                        -20 Energy cost
                         
                        Wounding Strike now hits up to 2 enemies. Strikes deal 20% more damage while Blood Lust is active.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                }
        );

    }
}
