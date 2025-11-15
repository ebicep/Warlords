package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.ContagiousFacade;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import javax.annotation.Nonnull;

public class ContagiousFacadeBranch extends AbstractUpgradeBranch<ContagiousFacade> {

    @Override
    public void runOnce() {
        ability.setStacksGranted(3);
    }
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
                            }, 4
                )
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
                            }, ability.getDamageAbsorption().addAdditiveModifier("Upgrade Branch", 0), 2.5f
                )
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Corrosive Facade",
                "Contagious Facade - Master Upgrade",
                """
                        +20% Cooldown Reduction
                        
                        When reactivating Contagious Facade, increase EPS by 10 for 8 seconds and Poisonous Hex infliction now affects all enemies within the radius.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Corrosive Facade", 0.8f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Polluting Guise",
                "Contagious Facade - Master Upgrade",
                """
                        +20% Cooldown Reduction
                        +20% Absorb Damage
                        
                        2.5x Shield Health
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Polluting Guise", 0.8f);
                    ability.getDamageAbsorption().addAdditiveModifier("Polluting Guise", 20f);
                }
        );
    }

}
