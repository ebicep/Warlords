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
        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+0.25% Leech Heal",
                5000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 0.25f);
                    ability.setLeechAllyAmount(allyLeech + 0.25f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+0.5% Leech Heal",
                10000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 0.5f);
                    ability.setLeechAllyAmount(allyLeech + 0.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+0.75% Leech Heal",
                15000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 0.75f);
                    ability.setLeechAllyAmount(allyLeech + 0.75f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+1% Leech Heal",
                20000,
                () -> {
                    ability.setLeechSelfAmount(selfLeech + 1);
                    ability.setLeechAllyAmount(allyLeech + 1);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Liquidizing Miasma",
                "Draining Miasma - Master Upgrade",
                "Draining Miasma deals 90% less damage but range and duration have been tripled." +
                        " Additionally, afflicted enemies will permanently have their damage reduced by 25% and" +
                        " will explode on death, dealing 1% max health damage to all nearby enemies.",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 3);
                    ability.setLeechDuration(ability.getLeechDuration() * 3);
                    ability.setEnemyHitRadius(ability.getEnemyHitRadius() * 3);

                    ability.setMaxHealthDamage((int) (ability.getMaxHealthDamage() * 0.1f));
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 0.1f);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 0.1f);

                    ability.setPveUpgrade(true);
                }
        );
    }
}
