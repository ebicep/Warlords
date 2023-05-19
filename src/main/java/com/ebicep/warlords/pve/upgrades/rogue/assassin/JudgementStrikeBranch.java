package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.JudgementStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class JudgementStrikeBranch extends AbstractUpgradeBranch<JudgementStrike> {

    float minDamage;
    float maxDamage;
    float strikeHeal = ability.getStrikeHeal();

    public JudgementStrikeBranch(AbilityTree abilityTree, JudgementStrike ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
            ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+15% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+30% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+45% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.45f);
                    ability.setMaxDamageHeal(maxDamage * 1.45f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+60% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.6f);
                    ability.setMaxDamageHeal(maxDamage * 1.6f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+100 Healing on strike kill.",
                5000,
                () -> {
                    ability.setStrikeHeal(100);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+200 Healing on strike kill.",
                10000,
                () -> {
                    ability.setStrikeHeal(strikeHeal + 200);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+300 Healing on strike kill.",
                15000,
                () -> {
                    ability.setStrikeHeal(strikeHeal + 300);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+400 Healing on strike kill.",
                20000,
                () -> {
                    ability.setStrikeHeal(strikeHeal + 400);
                }
        ));

        masterUpgrade = new Upgrade(
                "Death Strike",
                "Judgement Strike - Master Upgrade",
                "If the enemy hit by Judgement Strike drops below 30% max health they get executed. (Excluding boss mobs.)",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
