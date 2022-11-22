package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulSwitchBranch extends AbstractUpgradeBranch<SoulSwitch> {

    int radius = ability.getRadius();
    float cooldown;

    public SoulSwitchBranch(AbilityTree abilityTree, SoulSwitch ability) {
        super(abilityTree, ability);
        ability.setCooldown(ability.getCooldown() * 0.5f);
        cooldown = ability.getCooldown();

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
                "-40% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.6f);
                }
        ));

        treeB.add(new Upgrade(
                "Scope - Tier I",
                "+3 Blocks cast range",
                5000,
                () -> {
                    ability.setRadius(radius + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier II",
                "+6 Blocks cast range",
                10000,
                () -> {
                    ability.setRadius(radius + 6);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier III",
                "+9 Blocks cast range",
                15000,
                () -> {
                    ability.setRadius(radius + 9);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier IV",
                "+12 Blocks cast range",
                20000,
                () -> {
                    ability.setRadius(radius + 12);
                }
        ));

        masterUpgrade = new Upgrade(
                "Soul Burst",
                "Soul Switch - Master Upgrade",
                "Be able to swap with an enemy, leave a decoy on your original position that explodes after 3 seconds for 1004-1248 damage, nearby enemies are " +
                        "drawn to the decoy.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
