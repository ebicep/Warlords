package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.pve.upgrades.*;

public class CrusadersStrikeBranch extends AbstractUpgradeBranch<CrusadersStrike> {

    int energyGiven = ability.getEnergyGiven();

    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public CrusadersStrikeBranch(AbilityTree abilityTree, CrusadersStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Energy Given";
                    }

                    @Override
                    public void run(float value) {
                        ability.setEnergyGiven(energyGiven + (int) value);
                    }
                }, 1f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Crusader’s Slash",
                "Crusader's Strike - Master Upgrade",
                "Double the energy given to allies radius. Additionally, Crusader's Strike hits 2 additional enemies. (excluding energy given)",
                50000,
                () -> {
                    ability.setEnergyRadius(ability.getEnergyRadius() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Crusading Strike",
                "Crusader's Strike - Master Upgrade",
                """
                        -10 Energy cost
                                                
                        Strike crit chance is increased by 5%. Crit hits grant an additional 5 energy from strike while also providing a 10% speed increase to nearby allies for 2s.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -10);
                    ability.setCritChance(ability.getCritChance() + 5);
                }
        );
    }
}
