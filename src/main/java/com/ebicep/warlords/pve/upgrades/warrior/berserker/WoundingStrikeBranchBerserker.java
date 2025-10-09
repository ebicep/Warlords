package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class WoundingStrikeBranchBerserker extends AbstractUpgradeBranch<WoundingStrikeBerserker> {

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addMultiplicativeModifierAdd("PvE", .5f);
        damage.max().addMultiplicativeModifierAdd("PvE", .5f);
    }

    public WoundingStrikeBranchBerserker(AbilityTree abilityTree, WoundingStrikeBerserker ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 5f)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Wounding";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value);
                    }
                            }, ability.getWounding().addAdditiveModifier("Upgrade Branch", 0), 2f
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lacerating Strike",
                "Wounding Strike - Master Upgrade",
                """
                        -5 Energy cost
                        +50% Damage
                        
                        Wounding Strike now applies BLEED instead of wounding.

                        BLEED: Enemies afflicted take 100% more damage from Wounding Strike while Blood Lust is active. Bleeding enemies have their healing reduced by 80% and take 0.5% MAX HEALTH DAMAGE per second.""",
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -5);
                    Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
                    damage.min().addMultiplicativeModifierAdd("Master Upgrade Branch", .5f);
                    damage.max().addMultiplicativeModifierAdd("Master Upgrade Branch", .5f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Lustful Strike",
                "Wounding Strike - Master Upgrade",
                """
                        -10 Energy cost
                        
                        Wounding Strike now hits up to 3 enemies. Strikes deal 25% more damage while Blood Lust is active, additionally Blood lust healing is reduced by 25%.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -10);
                    abilityTree.getWarlordsPlayer().doOnStaticAbility(BloodLust.class, bloodLust -> {
                        bloodLust.setDamageConvertPercent(bloodLust.getDamageConvertPercent() - 25);
                    });
                }
        );

    }
}
