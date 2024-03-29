package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulbindingWeaponBranch extends AbstractUpgradeBranch<Soulbinding> {

    float cooldown = ability.getCooldown();
    int bindDuration = ability.getBindDuration();

    public SoulbindingWeaponBranch(AbilityTree abilityTree, Soulbinding ability) {
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
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+0.5s Bind duration",
                5000,
                () -> {
                    ability.setBindDuration(bindDuration + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+1s Bind duration",
                10000,
                () -> {
                    ability.setBindDuration(bindDuration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+1.5s Bind duration",
                15000,
                () -> {
                    ability.setBindDuration(bindDuration + 30);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+2s Bind duration",
                20000,
                () -> {
                    ability.setBindDuration(bindDuration + 40);
                }
        ));

        masterUpgrade = new Upgrade(
                "Curse Binding",
                "Soulbinding Weapon - Master Upgrade",
                "Gain 1 energy for each soulbound target hit by Fallen Souls and Spirit Link, increase your own and the allied ability\ncooldown reduction by 0.25s",
                50000,
                () -> {
                    ability.setSelfCooldownReduction(ability.getSelfCooldownReduction() + 0.25f);
                }
        );
    }
}
