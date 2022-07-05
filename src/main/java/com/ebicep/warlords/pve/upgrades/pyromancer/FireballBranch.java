package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FireballBranch extends AbstractUpgradeBranch<Fireball> {

    public FireballBranch(AbilityTree abilityTree, Fireball ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeB.add(new Upgrade("Speed - Tier I", "+20% Projectile speed", 5000));
        treeB.add(new Upgrade("Speed - Tier II", "+40% Projectile speed", 10000));
        treeB.add(new Upgrade("Speed - Tier III", "+80% Projectile speed", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+30 Blocks fall-off distance\n\nDirect hits apply the BURN status for 5 seconds.\n\nBURN: Enemies take 20% more damage from all sources\nand burn for 1% of their max health every second.",
                50000
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
        ability.setMaxFullDistance(ability.getMaxFullDistance() + 30);
        ability.setPveUpgrade(true);
    }
}
