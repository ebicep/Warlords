package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.TimeWarp;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class TimeWarpBranch extends AbstractUpgradeBranch<TimeWarp> {

    public TimeWarpBranch(AbilityTree abilityTree, TimeWarp ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+5% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+10% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+20% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Gain 20% speed while Time Warp is active.\n\nAdditionally, for each block traveled during Time\nWarp temporarily gain 1% Crit Chance and 2% Crit Multiplier\non all abilities.",
                50000
        );
    }

    float cooldown = ability.getCooldown();

    @Override
    public void a1() {
        ability.setCooldown(cooldown * 0.9f);
    }

    @Override
    public void a2() {
        ability.setCooldown(cooldown * 0.8f);
    }

    @Override
    public void a3() {
        ability.setCooldown(cooldown * 0.6f);
    }

    @Override
    public void b1() {

    }

    @Override
    public void b2() {

    }

    @Override
    public void b3() {

    }

    @Override
    public void c1() {
        ability.setWarpHealPercentage(35);
    }

    @Override
    public void c2() {
        ability.setWarpHealPercentage(40);
    }

    @Override
    public void c3() {
        ability.setWarpHealPercentage(50);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
