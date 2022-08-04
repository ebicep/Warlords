package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.HolyRadianceAvenger;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HolyRadianceBranchAvenger extends AbstractUpgradeBranch<HolyRadianceAvenger> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();

    public HolyRadianceBranchAvenger(AbilityTree abilityTree, HolyRadianceAvenger ability) {
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
                "-20% Cooldown reduction\n+1 Block hit radius",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setRadius(ability.getRadius() + 1);
                }
        ));

        treeB.add(new Upgrade(
                "Alleviate - Tier I",
                "+5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier II",
                "+10% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier III",
                "+15% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier IV",
                "+20% Healing\n+1 Block hit radius",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                    ability.setRadius(ability.getRadius() + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Marked targets take 50% more damage from\nAvenger's Strike and receive strike priority.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
