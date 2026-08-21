package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.WoundingStrikeDefender;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class WoundingStrikeBranchDefender extends AbstractUpgradeBranch<WoundingStrikeDefender> {

    public WoundingStrikeBranchDefender(AbilityTree abilityTree, WoundingStrikeDefender ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lacerating Strike",
                "Wounding Strike - Master Upgrade",
                """
                        +100% Crit chance
                        +50% Crit multiplier
                        
                        Critical Strikes grant you and nearby allies 30% damage reduction for 5 seconds.""",
                50000,
                () -> {
                    ability.getDamageValues().getStrikeDamage().critChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 100);
                    ability.getDamageValues().getStrikeDamage().critMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 50);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Shredding Strike",
                "Wounding Strike - Master Upgrade",
                """
                        -10 Energy cost
                        +800 Max health
                        
                        Wounding Strike now hits up to 3 enemies. Striking an enemy draws their aggro towards yourself and they will deal 15% less damage for 4 seconds. Additionally, if Last Stand is active, each Strike will increase the duration by 0.5 seconds (max 2.5s).
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -10);
                    abilityTree.getWarlordsPlayer().getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 800);
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
