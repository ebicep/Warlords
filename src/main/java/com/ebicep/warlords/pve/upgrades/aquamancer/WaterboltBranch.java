package com.ebicep.warlords.pve.upgrades.aquamancer;

import com.ebicep.warlords.abilties.WaterBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class WaterboltBranch extends UpgradeBranch<WaterBolt> {

    public WaterboltBranch(AbilityTree abilityTree, WaterBolt ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        upgrades.add(new Upgrade("Tier 1", "-10 Energy cost"));
        upgrades.add(new Upgrade("Tier 2", "+20% Healing"));
        upgrades.add(new Upgrade("Tier 3", "-10 Energy cost"));
        upgrades.add(new Upgrade("Tier 4", "+20% Healing"));
        upgrades.add(new Upgrade("Tier 5", "Shoot 2 bolts"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.2f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.2f);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.2f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.2f);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setShotsFiredAtATime(2);
    }
}
