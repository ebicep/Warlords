package com.ebicep.warlords.pve.upgrades.thunderlord;

import com.ebicep.warlords.abilties.Windfury;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class WindfuryBranch extends UpgradeBranch<Windfury> {
    public WindfuryBranch(AbilityTree abilityTree, Windfury ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        upgrades.add(new Upgrade("Tier I", "-50% Cooldown"));
        upgrades.add(new Upgrade("Tier II", "+1 Additional hit"));
        upgrades.add(new Upgrade("Tier III", "+20% Proc chance"));
        upgrades.add(new Upgrade("Tier IV", "+1 Additional hit"));
        upgrades.add(new Upgrade("Tier V", "+100% Damage per hit"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setCooldown(ability.getCooldown() * 0.5f);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setMaxHits(3);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setProcChance(55);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setMaxHits(4);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setWeaponDamage(235);
    }
}
