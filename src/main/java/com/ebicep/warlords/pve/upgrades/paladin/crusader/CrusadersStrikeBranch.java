package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class CrusadersStrikeBranch extends AbstractUpgradeBranch<CrusadersStrike> {

    int energyGiven = ability.getEnergyGiven();

    public CrusadersStrikeBranch(AbilityTree abilityTree, CrusadersStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 7.5f)
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
                """
                        Double the energy given to allies radius and increase the amount of allies that receive energy by 1 but reduce the energy given by 3.
                        
                        Additionally, Crusader's Strike hits 3 additional enemies. (excluding energy given)
                        """,
                50000,
                () -> {
                    ability.setEnergyRadius(ability.getEnergyRadius() * 2);
                    ability.setEnergyGiven(ability.getEnergyGiven() - 3);
                    ability.setEnergyMaxAllies(ability.getEnergyMaxAllies() + 1);
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
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -10);
                    ability.getDamageValues().getStrikeDamage().critChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 5);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
        damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
    }
}
