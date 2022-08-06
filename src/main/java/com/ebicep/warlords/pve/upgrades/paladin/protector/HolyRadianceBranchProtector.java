package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilties.HolyRadianceProtector;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HolyRadianceBranchProtector extends AbstractUpgradeBranch<HolyRadianceProtector> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();
    float markHealing = ability.getMarkHealing();

    public HolyRadianceBranchProtector(AbilityTree abilityTree, HolyRadianceProtector ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+7.5% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+15% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+22.5% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+30% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "+10% Additional mark healing",
                5000,
                () -> {
                    ability.setMarkHealing(markHealing + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "+20% Additional mark healing",
                10000,
                () -> {
                    ability.setMarkHealing(markHealing + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "+30% Additional mark healing",
                15000,
                () -> {
                    ability.setMarkHealing(markHealing + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "+40% Additional mark healing\n-10% Cooldown reduction",
                20000,
                () -> {
                    ability.setMarkHealing(markHealing + 40);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Edifying Radiance",
                "Holy Radiance - Master Upgrade",
                "Protector's Mark is now AoE.\n\nWarning: WIP",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
