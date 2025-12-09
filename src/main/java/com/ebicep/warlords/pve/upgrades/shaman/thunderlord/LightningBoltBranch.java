package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import javax.annotation.Nonnull;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {


    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getBoltDamage(), 12.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Nonnull
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Projectile Speed";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value / 100);
                    }},

                        ability.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Upgrade Branch", 0), 20f
                )
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgradeHitBox(ability, .25f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Lightning Volley",
                "Lightning Bolt - Master Upgrade",
                """
                        -10 Energy cost
                        
                        Lightning Bolt shoots two additional projectiles.""",
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -10);
                    ability.setShotsFiredAtATime(3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Electric Bolt",
                "Lightning Bolt - Master Upgrade",
                """
                        +10 Energy cost
                        
                        The first target hit takes 40% more damage, each subsequent enemy hit increases damage by an additional 10%.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 10);
                }
        );
    }
}
