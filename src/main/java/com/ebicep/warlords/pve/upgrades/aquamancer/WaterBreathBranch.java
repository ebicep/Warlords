package com.ebicep.warlords.pve.upgrades.aquamancer;

import com.ebicep.warlords.abilties.WaterBreath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class WaterBreathBranch extends UpgradeBranch<WaterBreath> {
    public WaterBreathBranch(AbilityTree abilityTree, WaterBreath ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "-30% Cooldown"));
        upgrades.add(new Upgrade("Tier 2", "+15% Knockback"));
        upgrades.add(new Upgrade("Tier 3", "+50% Healing"));
        upgrades.add(new Upgrade("Tier 4", "+15% Knockback"));
        upgrades.add(new Upgrade("Tier 5", "Remove energy cost\n+50% Healing"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setCooldown(ability.getCooldown() * 0.7f);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setVelocity(1.3);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() + (ability.getMinDamageHeal() * 0.5f));
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() + (ability.getMaxDamageHeal() * 0.5f));
    }

    @Override
    public void tierFourUpgrade() {
        ability.setVelocity(1.45);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setEnergyCost(0);
    }
}
