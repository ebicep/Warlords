package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.ImpalingStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ImpalingStrikeBranch extends AbstractUpgradeBranch<ImpalingStrike> {

    float minDamage;
    float maxDamage;
    float energyCost = ability.getEnergyCost();
    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

    public ImpalingStrikeBranch(AbilityTree abilityTree, ImpalingStrike ability) {
        super(abilityTree, ability);
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy Cost\n+1.25% Leech Heal",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                    ability.setLeechSelfAmount(selfLeech + 1.25f);
                    ability.setLeechAllyAmount(allyLeech + 1.25f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy Cost\n+2.5% Leech Heal",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                    ability.setLeechSelfAmount(selfLeech + 2.5f);
                    ability.setLeechAllyAmount(allyLeech + 2.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy Cost\n+3.75% Leech Heal",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setLeechSelfAmount(selfLeech + 3.75f);
                    ability.setLeechAllyAmount(allyLeech + 3.75f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy Cost\n+5% Leech Heal",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setLeechSelfAmount(selfLeech + 5);
                    ability.setLeechAllyAmount(allyLeech + 5);
                }
        ));

        masterUpgrade = new Upgrade(
                "Impaling Slash",
                "Impaling Strike - Master Upgrade",
                "Your Impaling Strikes deals double the\ndamage to enemies afflicted by LEECH",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
