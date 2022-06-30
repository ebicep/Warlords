package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.Inferno;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class InfernoBranch extends UpgradeBranch<Inferno> {

    public InfernoBranch(AbilityTree abilityTree, Inferno ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        upgrades.add(new Upgrade("Tier I", "+10% Crit Chance bonus"));
        upgrades.add(new Upgrade("Tier II", "+20% Crit Multiplier bonus"));
        upgrades.add(new Upgrade("Tier III", "+10% Crit Chance bonus"));
        upgrades.add(new Upgrade("Tier IV", "+20% Crit Multiplier bonus"));
        upgrades.add(new Upgrade("Tier V", "infERNo"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setCritChanceIncrease(ability.getCritChanceIncrease() + 10);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setCritMultiplierIncrease(ability.getCritMultiplierIncrease() + 20);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setCritChanceIncrease(ability.getCritChanceIncrease() + 10);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setCritMultiplierIncrease(ability.getCritMultiplierIncrease() + 20);
    }

    @Override
    public void tierFiveUpgrade() {
        //0 energy cost somethinbg
    }

}