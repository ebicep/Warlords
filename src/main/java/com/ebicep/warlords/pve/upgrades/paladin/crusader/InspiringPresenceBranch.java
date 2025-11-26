package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.InspiringPresence;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class InspiringPresenceBranch extends AbstractUpgradeBranch<InspiringPresence> {

    int energyPerSecond = ability.getEnergyPerSecond();

    public InspiringPresenceBranch(AbilityTree abilityTree, InspiringPresence ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.EnergyUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Energy per Second";
                    }

                    @Override
                    public void run(float value) {
                        ability.setEnergyPerSecond((int) (energyPerSecond + value));
                    }
                }, 2f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability)
                .addUpgradeHitBox(ability, 1.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Transcendent Presence",
                "Inspiring Presence - Master Upgrade",
                """
                        +10% Cooldown Reduction

                        Reduce the cooldown on all caster's and nearby allies' abilities by 15 seconds (excluding Inspiring Presence.) Additionally, allies gain 20% cooldown reduction for the duration of Inspiring Presence
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Transcendent Presence", 0.9f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Resilient Presence",
                "Inspiring Presence - Master Upgrade",
                """
                        +20% Cooldown Reduction
                        +25% Speed
                        +10% Passive damage reduction
                        
                        For the duration of Inspiring Presence, every hit you take restores 15 energy to you and any other ally affected by Inspiring Presence.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Resilient Presence", 0.8f);
                    ability.setSpeedBuff(ability.getSpeedBuff() + 25);
                    abilityTree.getWarlordsPlayer().getSpec().setDamageResistance(abilityTree.getWarlordsPlayer().getSpec().getDamageResistance() + 10);
                }
        );
    }
}
