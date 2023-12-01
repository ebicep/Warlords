package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.pve.upgrades.*;

import javax.annotation.Nonnull;

public class LastStandBranch extends AbstractUpgradeBranch<LastStand> {

    float selfDamageReduction = ability.getSelfDamageReduction();
    float allyDamageReduction;

    public LastStandBranch(AbilityTree abilityTree, LastStand ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setTeammateDamageReductionPercent(40);
        }
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
                        ability.setTeammateDamageReductionPercent((int) (allyDamageReduction + value));
                    }
                }, 3f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Final Stand",
                "Last Stand - Master Upgrade",
                """
                        +20% Cooldown Reduction
                                                
                        Double the radius of Last Stand and take 50% less knockback while active.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Final Stand", 0.8f);
                    ability.setRadius(ability.getRadius() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Enduring Defense",
                "Last Stand - Master Upgrade",
                """
                        +20% Cooldown Reduction
                                                
                        Double the radius of Last Stand and shred 15% of all enemies damage resistance when activated (excluding bosses).
                        For the duration of Last Stand, Seismic Wave and Ground Slam cooldowns' are reduced by 50% and Seismic Wave's energy cost is reduced to 30.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Enduring Defense", 0.8f);
                    ability.setRadius(ability.getRadius() * 2);
                }
        );
    }
}
