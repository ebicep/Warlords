package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class FireballBranch extends UpgradeBranch<Fireball> {

    public FireballBranch(AbilityTree abilityTree, Fireball ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "-5 Energy Cost"));
        upgrades.add(new Upgrade("Tier 2", "+10% Critical Chance\n+20% Critical Multiplier"));
        upgrades.add(new Upgrade("Tier 3", "-5 Energy Cost"));
        upgrades.add(new Upgrade("Tier 4", "+10% Critical Chance\n+20% Critical Multiplier"));
        upgrades.add(new Upgrade("Tier 5", "-15 Energy cost\n+20 Blocks fall-off distance\n\nApplies BURN status to directly-hit enemies for 5 seconds."));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setCritChance(ability.getCritChance() + 10);
        ability.setCritMultiplier(ability.getCritMultiplier() + 20);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setCritChance(ability.getCritChance() + 10);
        ability.setCritMultiplier(ability.getCritMultiplier() + 20);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setMaxFullDistance(ability.getMaxFullDistance() + 20);
        ability.setEnergyCost(ability.getEnergyCost() - 15);
        ability.setPveUpgrade(true);
    }

}
