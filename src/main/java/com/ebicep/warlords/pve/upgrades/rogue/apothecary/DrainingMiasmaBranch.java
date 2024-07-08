package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.DrainingMiasma;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;

public class DrainingMiasmaBranch extends AbstractUpgradeBranch<DrainingMiasma> {

    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

    public DrainingMiasmaBranch(AbilityTree abilityTree, DrainingMiasma ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.NamedUpgradeType() {

                    @Override
                    public String getName() {
                        return "Alleviate";
                    }

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
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Liquidizing Miasma",
                "Draining Miasma - Master Upgrade",
                "Draining Miasma deals 75% less damage but range and duration have been quadrupled." +
                        " Additionally, afflicted enemies will permanently have their damage reduced by 25% and" +
                        " will explode on death, dealing 1% max health damage to all nearby enemies.",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 4);
                    ability.setLeechDuration(ability.getLeechDuration() * 4);
                    ability.setRadius(ability.getRadius() * 4);

                    ability.setMaxHealthDamage((int) (ability.getMaxHealthDamage() * 0.25f));
                    Value.SetValue damage = ability.getDamageValues().getMiasmaDamage();
                    damage.value().addMultiplicativeModifierAdd("Master Upgrade Branch", -.75f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Toxic Immunity",
                "Draining Miasma - Master Upgrade",
                """
                        Draining Miasma deals 50% less damage but range and duration have been quadrupled. Allies within the radius will be healed for 2% of their max HP every second.
                        Additionally, the healing from Miasma can Overheal and all players, including the Apothecary, will be immune to debuffs for the duration.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 4);
                    ability.setLeechDuration(ability.getLeechDuration() * 4);
                    ability.setRadius(ability.getRadius() * 4);

                    ability.setMaxHealthDamage((int) (ability.getMaxHealthDamage() * 0.5f));
                    Value.SetValue damage = ability.getDamageValues().getMiasmaDamage();
                    damage.value().addMultiplicativeModifierAdd("Master Upgrade Branch", -.5f);
                }
        );
    }
}
