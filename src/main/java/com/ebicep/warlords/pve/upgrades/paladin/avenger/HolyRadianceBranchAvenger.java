package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.HolyRadianceAvenger;
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
                "-10% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-20% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-30% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.7f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-40% Cooldown reduction\n+1 Block hit radius",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.6f);
                    ability.setRadius(ability.getRadius() + 1);
                }
        ));

        treeB.add(new Upgrade(
                "Alleviate - Tier I",
                "+10% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier II",
                "+20% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier III",
                "+30% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier IV",
                "+40% Healing\n+1 Block hit radius",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                    ability.setRadius(ability.getRadius() + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Avenger's Mark is now AoE. Additionally, marked targets take 40% more damage from Avenger's Strike and receive strike priority.",
                50000,
                () -> {
                    ability.setPveMasterUpgrade(true);
                }
        );
    }
}
