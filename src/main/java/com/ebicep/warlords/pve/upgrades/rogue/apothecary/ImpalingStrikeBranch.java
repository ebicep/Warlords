package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.pve.upgrades.*;

public class ImpalingStrikeBranch extends AbstractUpgradeBranch<ImpalingStrike> {

    float minDamage;
    float maxDamage;
    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

    @Override
    public void runOnce() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
    }

    public ImpalingStrikeBranch(AbilityTree abilityTree, ImpalingStrike ability) {
        super(abilityTree, ability);
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();
        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 7.5f)
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
