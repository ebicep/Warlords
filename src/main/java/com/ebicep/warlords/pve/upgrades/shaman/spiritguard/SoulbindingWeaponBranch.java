package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilties.Soulbinding;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulbindingWeaponBranch extends AbstractUpgradeBranch<Soulbinding> {

    float cooldown = ability.getCooldown();
    float bindDuration = ability.getBindDuration();

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
                    ability.setBindDuration(bindDuration + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+1s Bind duration",
                10000,
                () -> {
                    ability.setBindDuration(bindDuration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+1.5s Bind duration",
                15000,
                () -> {
                    ability.setBindDuration(bindDuration + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+2s Bind duration",
                20000,
                () -> {
                    ability.setBindDuration(bindDuration + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Curse Binding",
                "Soulbinding Weapon - Master Upgrade",
                "Gain 1 energy for each soulbound\ntarget hit by Fallen Souls and Spirit\nLink, increase the allied ability\ncooldown reduction by 1s",
                50000,
                () -> {

                }
        );
    }
}
