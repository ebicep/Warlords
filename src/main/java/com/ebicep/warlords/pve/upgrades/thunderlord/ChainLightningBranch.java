package com.ebicep.warlords.pve.upgrades.thunderlord;

import com.ebicep.warlords.abilties.ChainLightning;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class ChainLightningBranch extends UpgradeBranch<ChainLightning> {
    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        upgrades.add(new Upgrade("Tier I", "-30% Cooldown"));
        upgrades.add(new Upgrade("Tier II", "+2 Chain bounces"));
        upgrades.add(new Upgrade("Tier III", "+50% Damage"));
        upgrades.add(new Upgrade("Tier IV", "+10 blocks bounce and cast range"));
        upgrades.add(new Upgrade("Tier V", "+2 Chain bounces"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setCooldown(ability.getCooldown() * 0.7f);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setMaxBounces(5);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.5f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.5f);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setBounceRange(20);
        ability.setRadius(30);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setMaxBounces(7);
    }
}
