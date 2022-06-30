package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.Inferno;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class InfernoBranch extends UpgradeBranch<Inferno> {

    public InfernoBranch(AbilityTree abilityTree, Inferno ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Crit Chance - Tier I", "+10% Crit Chance bonus", 5000));
        treeA.add(new Upgrade("Crit Chance - Tier II", "+20% Crit Chance bonus", 10000));
        treeA.add(new Upgrade("Crit Chance - Tier III", "+40% Crit Chance bonus", 20000));

        treeC.add(new Upgrade("Duration - Tier I", "+2s Duration", 5000));
        treeC.add(new Upgrade("Duration - Tier II", "+4s Duration", 10000));
        treeC.add(new Upgrade("Duration - Tier III", "+8s Duration", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Reduce the cooldown of Inferno by 1 second\nfor each enemy killed. (0.5s on assists)",
                500000
        );
    }

    @Override
    public void a1() {
        ability.setCritChanceIncrease(40);
    }

    @Override
    public void a2() {
        ability.setCritChanceIncrease(50);
    }

    @Override
    public void a3() {
        ability.setCritChanceIncrease(70);
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
        ability.setDuration(ability.getDuration() + 2);
    }

    @Override
    public void c2() {
        ability.setDuration(ability.getDuration() + 2);
    }

    @Override
    public void c3() {
        ability.setDuration(ability.getDuration() + 4);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
