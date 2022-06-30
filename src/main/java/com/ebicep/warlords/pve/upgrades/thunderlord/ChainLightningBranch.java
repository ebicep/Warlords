package com.ebicep.warlords.pve.upgrades.thunderlord;

import com.ebicep.warlords.abilties.ChainLightning;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class ChainLightningBranch extends UpgradeBranch<ChainLightning> {
    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Cooldown - Tier I", "-15% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-30% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-60% Cooldown reduction", 20000));

        treeB.add(new Upgrade("Utility - Tier I", "+10 Blocks bounce and cast range", 5000));
        treeB.add(new Upgrade("Utility - Tier II", "+2 Chain bounces", 10000));
        treeB.add(new Upgrade("Utility - Tier III", "+3 Chain bounces", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+20% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+40% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+80% Damage", 20000));

        masterUpgrade = new Upgrade("Master Upgrade", "master", 500000);
    }

    float cooldown = ability.getCooldown();

    @Override
    public void a1() {
        ability.setCooldown(cooldown * 0.85f);
    }

    @Override
    public void a2() {
        ability.setCooldown(cooldown * 0.7f);
    }

    @Override
    public void a3() {
        ability.setCooldown(cooldown * 0.4f);
    }

    @Override
    public void b1() {
        ability.setBounceRange(20);
        ability.setRadius(30);
    }

    @Override
    public void b2() {
        ability.setMaxBounces(5);
    }

    @Override
    public void b3() {
        ability.setMaxBounces(8);
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minDamage * 1.8f);
        ability.setMaxDamageHeal(maxDamage * 1.8f);
    }

    @Override
    public void master() {

    }
}
