package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.Repentance;
import com.ebicep.warlords.pve.upgrades.*;

public class RepentanceBranch extends AbstractUpgradeBranch<Repentance> {

    float damageConvert = ability.getDamageConvertPercent();

    public RepentanceBranch(AbilityTree abilityTree, Repentance ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Damage Conversion";
                    }

                    @Override
                    public void run(float value) {
                        ability.setDamageConvertPercent(damageConvert + value);
                    }
                }, .5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Revengeance",
                "Repentance - Master Upgrade",
                "Repentance's pool decay per second is reduced by 50% and the energy conversion based on damage taken is increased by 25%.",
                50000,
                () -> {
                    ability.setPoolDecay((int) (ability.getPoolDecay() * 0.5f));
                    ability.setEnergyConvertPercent(ability.getEnergyConvertPercent() * 1.25f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Remembrance",
                "Repentance - Master Upgrade",
                """
                        Repentance can now Overheal. Additionally, after Repentance ends, you gain an EPS buff equivalent to 1 EPS per 10 energy gained during Repentance for 5 seconds.
                        """,
                50000,
                () -> {

                }
        );
    }
}