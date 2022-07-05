package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilties.HealingRain;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HealingRainBranch extends AbstractUpgradeBranch<HealingRain> {

    public HealingRainBranch(AbilityTree abilityTree, HealingRain ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+2 Block radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+4 Block radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+8 Block radius", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+20% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+40% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+8s Duration\n\nUp to 5 enemies in Healing Rain will be struck with\nlightning for 288 - 406 damage every 2 seconds.",
                50000
        );
    }

    @Override
    public void a1() {
        ability.setRadius(10);
    }

    @Override
    public void a2() {
        ability.setRadius(12);
    }

    @Override
    public void a3() {
        ability.setRadius(16);
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

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minHealing * 1.1f);
        ability.setMaxDamageHeal(maxHealing * 1.1f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minHealing * 1.2f);
        ability.setMaxDamageHeal(maxHealing * 1.2f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minHealing * 1.4f);
        ability.setMaxDamageHeal(maxHealing * 1.4f);
    }

    @Override
    public void master() {
        ability.setDuration(20);
        ability.setPveUpgrade(true);
    }
}
