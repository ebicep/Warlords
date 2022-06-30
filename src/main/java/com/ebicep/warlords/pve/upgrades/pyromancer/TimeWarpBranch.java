package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.TimeWarp;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class TimeWarpBranch extends UpgradeBranch<TimeWarp> {

    public TimeWarpBranch(AbilityTree abilityTree, TimeWarp ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+5% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+10% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+20% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Gain 20% speed while Time Warp is active.",
                500000
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
