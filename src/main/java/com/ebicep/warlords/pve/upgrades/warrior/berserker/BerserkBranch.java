package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.pve.upgrades.*;

public class BerserkBranch extends AbstractUpgradeBranch<Berserk> {

    float damageBoost = ability.getDamageIncrease();
    int speedBuff = ability.getSpeedBuff();

    public BerserkBranch(AbilityTree abilityTree, Berserk ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return UpgradeTypes.DamageUpgradeType.super.getDescription0(value) + " Increase";
                    }

                    @Override
                    public void run(float value) {
                        ability.setDamageIncrease(damageBoost + value);
                    }
                }, 7.5f)
                .addTo(treeA);


        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.SpeedUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Speed";
                    }

                    @Override
                    public void run(float value) {
                        ability.setSpeedBuff((int) (speedBuff + value));
                    }
                }, 3f)
                .addUpgradeDuration(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Maniacal Rage",
                "Berserk - Master Upgrade",
                """
                        +10% Additional damage increase

                        Gain 0.2% Crit chance and Crit Multiplier for each instance of damage you deal to an enemy while Berserk is active. (Max 60%)""",
                50000,
                () -> {
                    ability.setDamageIncrease(ability.getDamageIncrease() + 10);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Visceral Rage",
                "Berserk - Master Upgrade",
                """
                        While Berserk is active, all damage you deal will stack up (max 20,000 damage). You may recast Berserk to convert all dealt damage into 0.5% energy and unleash a massive shockwave dealing 30% damage. The re-cast has unlimited uses.
                        """,
                50000,
                () -> {

                }
        );
    }
}
