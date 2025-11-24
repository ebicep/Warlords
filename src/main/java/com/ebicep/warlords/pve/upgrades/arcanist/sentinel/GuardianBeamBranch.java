package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.ArrayList;
import java.util.List;

public class GuardianBeamBranch extends AbstractUpgradeBranch<GuardianBeam> {

    public GuardianBeamBranch(AbilityTree abilityTree, GuardianBeam ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getBeamDamage(), 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+15 Block Range";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value);
                    }
                            }, ability.getMaxDistance().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Upgrade Branch", 0), 4
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Sentry Beam",
                "Guardian Beam - Master Upgrade",
                """
                        Enemy cooldowns are increased by an additional 3s. Additionally, shield health is increased by 75%.
                        """,
                50000,
                () -> {
                    ability.setRuneTimerIncrease(ability.getRuneTimerIncrease() + 3f);
                    ability.getShieldValues().replaceAll(integer -> (int) (integer * 1.75f));
                }
        );
        masterUpgrade2 = new Upgrade(
                "Conservator Beam",
                "Guardian Beam - Master Upgrade",
                """
                        +10 Blocks range
                        +25% Damage
                        
                        When Guardian Beam hits an enemy, reduce their speed by 25% for 5s. Additionally, when Guardian Beam hits an ally, reduce their cooldowns by 1.5 seconds.
                        """,
                50000,
                () -> {
                    Value.RangedValueCritable damage = ability.getDamageValues().getBeamDamage();
                    damage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .25f);
                    damage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .25f);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getBeamDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        damage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        ability.setShieldValues(new ArrayList<>(List.of(600, 1200, 2400)));
    }

}
