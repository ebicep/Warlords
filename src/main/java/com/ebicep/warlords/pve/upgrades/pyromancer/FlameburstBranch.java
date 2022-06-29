package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.FlameBurst;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class FlameburstBranch extends UpgradeBranch<FlameBurst> {

    public FlameburstBranch(AbilityTree abilityTree, FlameBurst ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "-30% Cooldown"));
        upgrades.add(new Upgrade("Tier 2", "+1 block hit radius"));
        upgrades.add(new Upgrade("Tier 3", "+50% Crit Multiplier"));
        upgrades.add(new Upgrade("Tier 4", "+1 block hit radius"));
        upgrades.add(new Upgrade("Tier 5", "Remove energy cost\n\nFlameburst gains +0.5% Crit chance and +1% Crit Multiplier per block travelled."));
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
        ability.setEnergyCost(0);
        ability.setPveUpgrade(true);
    }

}
