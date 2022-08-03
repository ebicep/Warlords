package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulSwitchBranch extends AbstractUpgradeBranch<SoulSwitch> {

    public SoulSwitchBranch(AbilityTree abilityTree, SoulSwitch ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+4 Block radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+8 Block radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+16 Block radius", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER",
                50000
        );
    }

    int radius = ability.getRadius();

    float cooldown = ability.getCooldown();

    @Override
    public void c1() {
        ability.setCooldown(cooldown * 0.85f);
    }

    @Override
    public void c2() {
        ability.setCooldown(cooldown * 0.7f);
    }

    @Override
    public void c3() {
        ability.setCooldown(cooldown * 0.4f);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {

    }
}
