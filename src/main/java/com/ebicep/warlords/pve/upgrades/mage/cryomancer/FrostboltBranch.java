package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilties.FrostBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FrostboltBranch extends AbstractUpgradeBranch<FrostBolt> {

    public FrostboltBranch(AbilityTree abilityTree, FrostBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeB.add(new Upgrade("Speed - Tier I", "+20% Projectile Speed", 5000));
        treeB.add(new Upgrade("Speed - Tier II", "+40% Projectile Speed", 10000));
        treeB.add(new Upgrade("Speed - Tier III", "+80% Projectile Speed", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+10% Slowness\n\nDirectly-hit enemies shatter after 1.5 seconds,\ndealing 309 - 454 damage to all nearby enemies\nand slow them by 35% for 2 seconds.",
                50000
        );
    }

    int energyCost = ability.getEnergyCost();

    @Override
    public void a1() {
        ability.setEnergyCost(energyCost - 10);
    }

    @Override
    public void a2() {
        ability.setEnergyCost(energyCost - 20);
    }

    @Override
    public void a3() {
        ability.setEnergyCost(energyCost - 30);
    }

    double projectileSpeed = ability.getProjectileSpeed();

    @Override
    public void b1() {
        ability.setProjectileSpeed(projectileSpeed * 1.2);
    }

    @Override
    public void b2() {
        ability.setProjectileSpeed(projectileSpeed * 1.4);
    }

    @Override
    public void b3() {
        ability.setProjectileSpeed(projectileSpeed * 1.8);
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minDamage * 1.1f);
        ability.setMaxDamageHeal(maxDamage * 1.1f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    @Override
    public void master() {
        ability.setSlowness(ability.getSlowness() + 10);
        ability.setPveUpgrade(true);
    }
}
