package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.LightningBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {

    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Impair - Tier I", "+10% Damage\n+25% Projectile speed", 5000));
        treeA.add(new Upgrade("Impair - Tier II", "+20% Damage\n+50% Projectile speed", 10000));
        treeA.add(new Upgrade("Impair - Tier III", "+30% Damage\n+75% Projectile speed", 15000));
        treeA.add(new Upgrade("Impair - Tier IV", "+40% Damage\n+100% Projectile speed", 20000));

        treeC.add(new Upgrade("Spark - Tier I", "-5 Energy cost\n+0.25 Block hit radius", 5000));
        treeC.add(new Upgrade("Spark - Tier II", "-10 Energy cost\n+0.5 Block hit radius", 10000));
        treeC.add(new Upgrade("Spark - Tier III", "-15 Energy cost\n+0.75 Block hit radius", 15000));
        treeC.add(new Upgrade("Spark - Tier IV", "-20 Energy cost\n+1 Block hit radius", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Lightning Bolt shoots two additional projectiles.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void a1() {
        ability.setMinDamageHeal(minDamage * 1.1f);
        ability.setMaxDamageHeal(maxDamage * 1.1f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    int energyCost = ability.getEnergyCost();

    @Override
    public void b1() {
        ability.setEnergyCost(energyCost - 5);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(energyCost - 10);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(energyCost - 15);
    }

    @Override
    public void c1() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 10);
    }

    @Override
    public void c2() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 10);
    }

    @Override
    public void c3() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 20);
    }

    @Override
    public void master() {
        ability.setShotsFiredAtATime(3);
    }
}
