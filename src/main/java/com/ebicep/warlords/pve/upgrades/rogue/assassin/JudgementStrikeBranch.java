package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.JudgementStrike;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class JudgementStrikeBranch extends AbstractUpgradeBranch<JudgementStrike> {


    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addMultiplicativeModifierAdd("PvE", .3f);
        damage.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public JudgementStrikeBranch(AbilityTree abilityTree, JudgementStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Healing on Strike Kill";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value);
                    }
                }, ability.getHealValues().getStrikeHealing().value().addAdditiveModifier("Upgrade Branch", 0), 100f)
                .addUpgradeEnergy(ability, 5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Death Strike",
                "Judgement Strike - Master Upgrade",
                """
                        +100 Healing on Strike Kill
                        -10 Energy cost
                                                
                        Each strike deals 1% of the target's max health as bonus damage.""",
                50000,
                () -> {
                    ability.getHealValues().getStrikeHealing().value().addAdditiveModifier("Master Upgrade Branch", 100);
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -10);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Judgemental Fury",
                "Judgement Strike - Master Upgrade",
                """
                        +45% Crit multiplier
                                                
                        Judgement Strike will now hit twice in one use, the second strike is counted as an additional strike for a guarantee crit"
                        """,
                50000,
                () -> {
                    ability.getDamageValues().getStrikeDamage().critMultiplier().addAdditiveModifier("Master Upgrade Branch", 45);
                }
        );
    }
}
