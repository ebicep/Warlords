package com.ebicep.warlords.pve.upgrades.thunderlord;

import com.ebicep.warlords.abilties.LightningBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class LightningBoltBranch extends UpgradeBranch<LightningBolt> {
    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        upgrades.add(new Upgrade("Tier I", "-20 Energy Cost\n+20% Damage"));
        upgrades.add(new Upgrade("Tier II", "-10 Energy Cost\n+20 Blocks fall-off distance"));
        upgrades.add(new Upgrade("Tier III", "Shoot 3 bolts instead of 1"));
        upgrades.add(new Upgrade("Tier IV", "+50% Damage"));
        upgrades.add(new Upgrade("Tier V", "Shoot 5 bolts instead of 3"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 20);
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.2f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.2f);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setShotsFiredAtATime(3);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.5f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.5f);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setShotsFiredAtATime(5);
    }
}
