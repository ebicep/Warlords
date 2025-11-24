package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import javax.annotation.Nonnull;

public class LastStandBranch extends AbstractUpgradeBranch<LastStand> {

    float selfDamageReduction = ability.getSelfDamageReduction();
    float allyDamageReduction;

    @Override
    public void runOnce() {
        ability.setTeammateDamageReductionPercent(40);
    }

    public LastStandBranch(AbilityTree abilityTree, LastStand ability) {
        super(abilityTree, ability);

        allyDamageReduction = ability.getTeammateDamageReduction();

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.ShieldUpgradeType() {
                    @Nonnull
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Damage Reduction";
                    }

                    @Override
                    public void run(float value) {
                        ability.setSelfDamageReductionPercent((int) (selfDamageReduction + value));
                    }
                }, 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Final Stand",
                "Last Stand - Master Upgrade",
                """
                        +25% Cooldown Reduction
                        +25% Self Damage Reduction
                        
                        Double the radius of Last Stand and take 50% less knockback while active.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Final Stand", 0.75f);
                    ability.setSelfDamageReductionPercent(ability.getSelfDamageReduction() + 25);
                    ability.setRadius(ability.getRadius() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Enduring Defense",
                "Last Stand - Master Upgrade",
                """
                        +25% Cooldown Reduction
                        +15% Ally Damage Reduction
                        
                        Double the radius of Last Stand and Seismic Wave and Ground Slam cooldowns' are reduced by 50% and Seismic Wave's energy cost is reduced to 30 while active.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Enduring Defense", 0.75f);
                    ability.setTeammateDamageReductionPercent(ability.getTeammateDamageReduction() + 15);
                    ability.setRadius(ability.getRadius() * 2);
                }
        );
    }
}
