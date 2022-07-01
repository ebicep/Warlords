package com.ebicep.warlords.pve.upgrades.aquamancer;

import com.ebicep.warlords.abilties.WaterBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WaterBoltBranch extends AbstractUpgradeBranch<WaterBolt> {

    public WaterBoltBranch(AbilityTree abilityTree, WaterBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeC.add(new Upgrade("Damage/Healing - Tier I", "+10% Damage and Healing", 5000));
        treeC.add(new Upgrade("Damage/Healing - Tier II", "+20% Damage and Healing", 10000));
        treeC.add(new Upgrade("Damage/Healing - Tier III", "+40% Damage and Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+100% Projectile speed\n\nWater Bolt increases the damage dealt\nof all allies it hits by 15% for 10 seconds.",
                500000
        );
    }

    @Override
    public void a1() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void a2() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void a3() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
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
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minHealing * 1.1f);
        ability.setMaxDamageHeal(maxHealing * 1.1f);
        ability.setMinDamage(minDamage * 1.1f);
        ability.setMaxDamage(maxDamage * 1.1f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minHealing * 1.2f);
        ability.setMaxDamageHeal(maxHealing * 1.2f);
        ability.setMinDamage(minDamage * 1.2f);
        ability.setMaxDamage(maxDamage * 1.2f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minHealing * 1.4f);
        ability.setMaxDamageHeal(maxHealing * 1.4f);
        ability.setMinDamage(minDamage * 1.4f);
        ability.setMaxDamage(maxDamage * 1.4f);
    }

    @Override
    public void master() {
        ability.setProjectileSpeed(ability.getProjectileSpeed() * 2);
        ability.setPveUpgrade(true);
    }
}
