package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.CapacitorTotem;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class CapacitorTotemBranch extends AbstractUpgradeBranch<CapacitorTotem> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();
    int duration = ability.getTickDuration();

    public CapacitorTotemBranch(AbilityTree abilityTree, CapacitorTotem ability) {
        super(abilityTree, ability);

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
                "Chronos - Tier I",
                "+1s Duration\n-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setTickDuration(duration + 20);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+2s Duration\n-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setTickDuration(duration + 40);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+3s Duration\n-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setTickDuration(duration + 60);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+4s Duration\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Incapacitating Totem",
                "Capacitor Totem - Master Upgrade",
                "Each Capacitor Totem proc increases the hit radius by 0.5 Blocks and all enemies hit have their damage resistance permanently reduced by 20%",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }

}
