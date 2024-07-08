package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.ContagiousFacade;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import javax.annotation.Nonnull;

public class ContagiousFacadeBranch extends AbstractUpgradeBranch<ContagiousFacade> {

    public ContagiousFacadeBranch(AbilityTree abilityTree, ContagiousFacade ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+2.5s Shield Duration";
                    }

                    @Override
                    public void run(float value) {
                        ability.setShieldTickDuration(ability.getShieldTickDuration() + 50);
                    }
                }, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.NamedUpgradeType() {
                    @Override
                    public String getName() {
                        return "Impair";
                    }

                    @Nonnull
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Absorb Damage";
                    }

                    @Override
                    public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                        modifier.setModifier(value);
                    }
                }, ability.getDamageAbsorption().addAdditiveModifier("Upgrade Branch", 0), 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Corrosive Facade",
                "Contagious Facade - Master Upgrade",
                """
                        Your shield also deals the value of damage absorbed to all nearby enemies and slows them by 25% for 3s.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Polluting Guise",
                "Contagious Facade - Master Upgrade",
                """
                        2x Absorb Damage
                                                
                        Total damage absorbed is capped at the user's max hp.
                        """,
                50000,
                () -> {
                    ability.getDamageAbsorption().addMultiplicativeModifierMult("Master Upgrade Branch", 2);
                }
        );
    }

}
