package com.ebicep.warlords.pve.upgrades.thunderlord;

import com.ebicep.warlords.abilties.ChainLightning;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class ChainLightningBranch extends UpgradeBranch<ChainLightning> {
    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Tier I", "-10 Energy cost"));
        treeA.add(new Upgrade("Tier II", "-20 Energy cost"));
        treeA.add(new Upgrade("Tier III", "-30 Energy cost"));

        treeB.add(new Upgrade("Tier I", "+10 Blocks bounce and cast range"));
        treeB.add(new Upgrade("Tier II", "+2 Chain bounces"));
        treeB.add(new Upgrade("Tier III", "+2 Chain bounces"));

        treeC.add(new Upgrade("Tier I", "+10 blocks bounce and cast range"));
        treeC.add(new Upgrade("Tier II", "+2 Chain bounces"));
        treeC.add(new Upgrade("Tier III", "+2 Chain bounces"));

        masterUpgrade = new Upgrade("Master Upgrade", "master");
    }

    @Override
    public void a1() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void a2() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void a3() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void b1() {
        ability.setBounceRange(20);
        ability.setRadius(30);
    }

    @Override
    public void b2() {
        ability.setMaxBounces(7);
    }

    @Override
    public void b3() {

    }

    @Override
    public void c1() {

    }

    @Override
    public void c2() {

    }

    @Override
    public void c3() {

    }

    @Override
    public void master() {

    }
}
