package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilties.FlameBurst;
import org.bukkit.inventory.ItemStack;

public class FlameburstUpgradeBranch extends UpgradeBranch<FlameBurst> {

    public FlameburstUpgradeBranch(AbilityTree abilityTree, FlameBurst ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "-30% Cooldown"));
        upgrades.add(new Upgrade("Tier 2", "+1 block hit radius"));
        upgrades.add(new Upgrade("Tier 3", "+50% Critical Damage"));
        upgrades.add(new Upgrade("Tier 4", "+1 block hit radius"));
        upgrades.add(new Upgrade("Tier 5", "-60 Energy cost"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setCooldown(ability.getCooldown() * .7f);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setHitbox(6);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 50);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setHitbox(7);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 60);
    }

}
