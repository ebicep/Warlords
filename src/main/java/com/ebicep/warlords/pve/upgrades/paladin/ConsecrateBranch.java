package com.ebicep.warlords.pve.upgrades.paladin;

import com.ebicep.warlords.abilties.Consecrate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ConsecrateBranch extends AbstractUpgradeBranch<Consecrate> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float radius = ability.getRadius();
    float cooldown = ability.getCooldown();

    public ConsecrateBranch(AbilityTree abilityTree, Consecrate ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Damage\n-15% Cooldown reduction",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+0.25 Blocks hit radius",
                5000,
                () -> {
                    ability.setRadius(radius + 0.25f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+0.5 Blocks hit radius",
                10000,
                () -> {
                    ability.setRadius(radius + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+0.75 Blocks hit radius",
                15000,
                () -> {
                    ability.setRadius(radius + 0.75f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+1 Blocks hit radius\n+1s Duration",
                20000,
                () -> {
                    ability.setRadius(radius + 1);
                    ability.setDuration(ability.getDuration() + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Sanctify",
                "Consecrate - Master Upgrade",
                "-30 Energy cost\n+2 Additional blocks hit radius\n-20% Cooldown reduction",
                50000,
                () -> {
                    ability.setEnergyCost(ability.getEnergyCost() - 30);
                    ability.setRadius(ability.getRadius() - 2);
                    ability.setCooldown(ability.getCooldown() * 0.9f);
                }
        );
    }
}
