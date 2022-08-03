package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.CapacitorTotem;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class CapacitorTotemBranch extends AbstractUpgradeBranch<CapacitorTotem> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();
    int duration = ability.getDuration();
    double radius = ability.getRadius();

    public CapacitorTotemBranch(AbilityTree abilityTree, CapacitorTotem ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Damage\n-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Damage\n-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Damage\n-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Damage\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration\n+0.5 Blocks hit radius",
                5000,
                () -> {
                    ability.setDuration(duration + 1);
                    ability.setRadius(radius + 0.5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration\n+1 Blocks hit radius",
                10000,
                () -> {
                    ability.setDuration(duration + 2);
                    ability.setRadius(radius + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration\n+1.5 Blocks hit radius",
                15000,
                () -> {
                    ability.setDuration(duration + 3);
                    ability.setRadius(radius + 1.5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration\n+2 Blocks hit radius",
                20000,
                () -> {
                    ability.setDuration(duration + 4);
                    ability.setRadius(radius + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Incapacitating Totem",
                "Capacitor Totem - Master Upgrade",
                "Each Capacitor Totem procs increases the hit radius\nby 0.25 Blocks and all enemies hit have their damage\nresistance permanently reduced by 20%",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }

    @Override
    public void c1() {
        ability.setRadius(ability.getRadius() + 1);
    }

    @Override
    public void c2() {
        ability.setRadius(ability.getRadius() + 1);
    }

    @Override
    public void c3() {
        ability.setRadius(ability.getRadius() + 1);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setDuration((int) (ability.getDuration() * 1.5f));
        ability.setPveUpgrade(true);
    }
}
