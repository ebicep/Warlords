package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.DrainingMiasma;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class DrainingMiasmaBranch extends AbstractUpgradeBranch<DrainingMiasma> {

    float cooldown = ability.getCooldown();
    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

    public DrainingMiasmaBranch(AbilityTree abilityTree, DrainingMiasma ability) {
        super(abilityTree, ability);
        treeB.add(new Upgrade(
                "Alleviate - Tier I",
                "+1.25% Leech Heal",
                5000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 1.25f);
                    ability.setLeechAllyAmount(allyLeech + 1.25f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier II",
                "+2.5% Leech Heal",
                10000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 2.5f);
                    ability.setLeechAllyAmount(allyLeech + 2.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier III",
                "+3.75% Leech Heal",
                15000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 3.75f);
                    ability.setLeechAllyAmount(allyLeech + 3.75f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier IV",
                "+5% Leech Heal",
                20000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 5);
                    ability.setLeechAllyAmount(allyLeech + 5);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Liquidizing Miasma",
                "Draining Miasma - Master Upgrade",
                "Draining Miasma deals 75% less damage but range and duration have been quadrupled." +
                        " Additionally, afflicted enemies will permanently have their damage reduced by 30% and" +
                        " will explode on death, dealing 2% max health damage to all nearby enemies.",
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() * 4);
                    ability.setLeechDuration(ability.getLeechDuration() * 4);
                    ability.setEnemyHitRadius(ability.getEnemyHitRadius() * 4);

                    ability.setMaxHealthDamage((int) (ability.getMaxHealthDamage() * 0.25f));
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 0.25f);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 0.25f);

                    ability.setPveUpgrade(true);
                }
        );
    }
}
