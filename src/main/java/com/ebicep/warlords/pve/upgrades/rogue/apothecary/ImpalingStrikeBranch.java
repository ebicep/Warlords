package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.pve.upgrades.*;

public class ImpalingStrikeBranch extends AbstractUpgradeBranch<ImpalingStrike> {

    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public ImpalingStrikeBranch(AbilityTree abilityTree, ImpalingStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Leech Heal";
                    }

                    @Override
                    public void run(float value) {
                        ability.setLeechSelfAmount(selfLeech + value);
                        ability.setLeechAllyAmount(allyLeech + value);
                    }
                }, 1f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Impaling Slash",
                "Impaling Strike - Master Upgrade",
                "-20 Additional energy cost\n\nYour Impaling Strikes deals triple the damage to enemies afflicted by LEECH",
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Impaling Assault",
                "Impaling Strike - Master Upgrade",
                """
                        -20 Energy cost
                                                
                        Impaling Strike now hits 2 additional targets.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addAdditiveModifier("Master Upgrade Branch", -20);
                }
        );
    }
}
