package com.ebicep.warlords.pve.upgrades.thunderlord;

import com.ebicep.warlords.abilties.LightningBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {

    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeB.add(new Upgrade("Utility - Tier I", "+1 Projectile", 5000));
        treeB.add(new Upgrade("Utility - Tier II", "+2 Projectiles", 10000));
        treeB.add(new Upgrade("Utility - Tier III", "+3 Projectiles", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+20% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+40% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+80% Damage", 20000));

        masterUpgrade = new Upgrade("Master Upgrade", "master", 500000);
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
        ability.setShotsFiredAtATime(2);
    }

    @Override
    public void b2() {
        ability.setShotsFiredAtATime(3);
    }

    @Override
    public void b3() {
        ability.setShotsFiredAtATime(4);
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minDamage * 1.8f);
        ability.setMaxDamageHeal(maxDamage * 1.8f);
    }

    @Override
    public void master() {

    }
}
