package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.TimeWarp;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class TimewarpBranch extends UpgradeBranch<TimeWarp> {

    public TimewarpBranch(AbilityTree abilityTree, TimeWarp ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "+10% Healing, +10% Speed while warping"));
        upgrades.add(new Upgrade("Tier 2", "-20% Cooldown"));
        upgrades.add(new Upgrade("Tier 3", "+10% Healing, +10 Speed while warping"));
        upgrades.add(new Upgrade("Tier 4", "-20% Cooldown"));
        upgrades.add(new Upgrade("Tier 5", "warp or something i dont know"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setWarpHealPercentage(40);
        ability.setPveUpgrade(true);
        ability.setPveSpeed(10);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setCooldown(ability.getCooldown() * .8f);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setWarpHealPercentage(50);
        ability.setPveSpeed(20);
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
