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

    @Override
    public void a1() {
        ability.setMinDamageHeal(minHealing * 1.15f);
        ability.setMaxDamageHeal(maxHealing * 1.15f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minHealing * 1.3f);
        ability.setMaxDamageHeal(maxHealing * 1.3f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minHealing * 1.6f);
        ability.setMaxDamageHeal(maxHealing * 1.6f);
    }

    @Override
    public void a4() {

    }

    @Override
    public void b1() {

    }

    @Override
    public void b2() {

    }

    @Override
    public void b3() {

    }

    @Override
    public void b4() {

    }

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
