package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.ChainHeal;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ChainHealBranch extends AbstractUpgradeBranch<ChainHeal> {
    public ChainHealBranch(AbilityTree abilityTree, ChainHeal ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+15% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+30% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+60% Healing", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+5 Blocks cast and bounce range\n\nIncrease Chain Heal healing by 15% per bounce",
                50000
        );
    }

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    float cooldown = ability.getCooldown();

    @Override
    public void c1() {
        ability.setCooldown(cooldown * 0.9f);
    }

    @Override
    public void c2() {
        ability.setCooldown(cooldown * 0.8f);
    }

    @Override
    public void c3() {
        ability.setCooldown(cooldown * 0.6f);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setBounceRange(ability.getBounceRange() + 5);
        ability.setRadius(ability.getRadius() + 5);
        ability.setPveUpgrade(true);
    }
}
