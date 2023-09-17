package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.ContagiousFacade;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ContagiousFacadeBranch extends AbstractUpgradeBranch<ContagiousFacade> {

    float cooldown = ability.getCooldown();
    int shieldTickDuration = ability.getShieldTickDuration();
    float damageAbsorption = ability.getDamageAbsorption();


    public ContagiousFacadeBranch(AbilityTree abilityTree, ContagiousFacade ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction\n+2.5s Shield duration",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setShieldTickDuration(shieldTickDuration + 50);
                }
        ));

        treeB.add(new Upgrade(
                "Impair - Tier I",
                "+2.5% Absorb damage",
                5000,
                () -> {
                    ability.setDamageAbsorption(damageAbsorption + 2.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier II",
                "+5% Absorb damage",
                10000,
                () -> {
                    ability.setDamageAbsorption(damageAbsorption + 5);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier III",
                "+7.5% Absorb damage",
                15000,
                () -> {
                    ability.setDamageAbsorption(damageAbsorption + 7.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Impair - Tier IV",
                "+10% Absorb damage",
                20000,
                () -> {
                    ability.setDamageAbsorption(damageAbsorption + 10);
                }
        ));

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
                    ability.setDamageAbsorption(ability.getDamageAbsorption() * 2);
                }
        );
    }

}
