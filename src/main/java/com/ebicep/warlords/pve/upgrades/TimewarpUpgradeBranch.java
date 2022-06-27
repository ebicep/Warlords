package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilties.TimeWarp;
import org.bukkit.inventory.ItemStack;

public class TimewarpUpgradeBranch extends UpgradeBranch<TimeWarp> {

    public TimewarpUpgradeBranch(AbilityTree abilityTree, TimeWarp ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "+10% Healing, +10% Speed while warping"));
        upgrades.add(new Upgrade("Tier 2", "-20% Cooldown"));
        upgrades.add(new Upgrade("Tier 3", "+10% Healing, +10 Speed while warping"));
        upgrades.add(new Upgrade("Tier 4", "-20% Cooldown"));
        upgrades.add(new Upgrade("Tier 5", "warp something"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setWarpHealPercentage(40);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setCooldown(ability.getCooldown() * .8f);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setWarpHealPercentage(50);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setCooldown(ability.getCooldown() * .6f);
    }

    @Override
    public void tierFiveUpgrade() {
        //0 energy cost somethinbg
    }

}
